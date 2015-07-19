package me.loganbwde.KostenlosSchild;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;


public class main extends JavaPlugin implements Listener
{
	 ArrayList<String> Players = new ArrayList<String>();
	
	 public int zahl;
	 public int task;
	 public int nid;
	 public int nnumber;
	 Material mat;
	 String name;
	
	 String prefix = getConfig().getString("Config.prefix");
	 String noperm = getConfig().getString("Config.noperm");
	 String missingid = getConfig().getString("Config.missingid");
	 String missingnumber = getConfig().getString("Config.missingnumber");
	 String error = getConfig().getString("Config.error");
	 String zeit1 = getConfig().getString("Config.time1");
	 String zeit2 = getConfig().getString("Config.time2");
	 String configrl = getConfig().getString("Config.configrl");
	 String invname = getConfig().getString("Config.invname");
	 int sek = getConfig().getInt("Config.sek");
	
	@Override
	 public void onEnable()
	 {
		this.getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
		System.out.println("[KostenlosSchild] Plugin aktiviert.");	
	 }
	 public void onDisable()
	 {
		System.out.println("[KostenlosSchild] Plugin deaktiviert.");	
	 }
	 
	 public boolean onCommand(CommandSender sender,Command cmd,String cmdlabel,String[] args)
		{
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("ksreload"))
			{
				if(p.hasPermission("kostenlosschild.reload"))
				{
					reloadConfig();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', configrl));
				}
				else
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', noperm));
				}
			}
			return true;
		}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	 public void onSignCreate(SignChangeEvent event)
	 {
	   Player p = event.getPlayer();
	   if (event.getLine(0).equalsIgnoreCase(prefix))
	   {
		   if (!p.hasPermission("KostenlosSchild.create"))
		   {
			   p.sendMessage(ChatColor.translateAlternateColorCodes('&', noperm));
			   event.getBlock().setType(Material.AIR);
			   return;
		   }
	       if (event.getLine(1).equals(""))
	       {
	           p.sendMessage(ChatColor.translateAlternateColorCodes('&', missingid));
	    	   event.getBlock().setType(Material.AIR);
	    	   return;
	       }
	       if (event.getLine(2).equals(""))
	       {
	           p.sendMessage(ChatColor.translateAlternateColorCodes('&', missingnumber));
	    	   event.getBlock().setType(Material.AIR);
	    	   return;
	       }
	       Sign schild = (Sign)event.getBlock().getState();
	       String id = event.getLine(1);
	       String number = event.getLine(2);
	       nid = Integer.parseInt(id);
	       nnumber = Integer.parseInt(number);
	       ItemStack stack = new ItemStack(nid);
	       mat  = stack.getType();
	       event.setLine(0, "§b" + prefix);
	       event.setLine(1, "§c" + mat);
	       event.setLine(2, number + "§bx");
	       schild.update(true);
	   }
	 }
	@EventHandler
	 public void onInteract(PlayerInteractEvent event)
	  {
	    Player p = event.getPlayer();
	    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getState() instanceof Sign))
	    {
	      Sign schild = (Sign)event.getClickedBlock().getState();
	      if (schild.getLine(0).equalsIgnoreCase("§b" + prefix)) {
	        try
	        {
	          if (!p.hasPermission("KostenlosSchild.use"))
	          {
	        	  p.sendMessage(ChatColor.translateAlternateColorCodes('&', noperm));
	        	  return;
	          }
	          if(Players.contains(name))
	          {
	        	  p.sendMessage(ChatColor.translateAlternateColorCodes('&', zeit1 + " " + zahl + " " + zeit2));
	          }
	          else
	          {
	        	  p.openInventory(createInventory(nid,nnumber));
	          }
	        }
	        catch (NumberFormatException e)
	        {
	          p.sendMessage(error);
	        }
	      }
	    }
	  }
	  
    @SuppressWarnings("deprecation")
	 public Inventory createInventory(int id,int number)
	 {
	    Inventory invent = Bukkit.createInventory(null, 9,"§b" + invname);
	    for (int i = 0; i < invent.getSize(); i++) 
	    {
	    	invent.setItem(4, new ItemStack(id, number));
	    }
	    return invent;
	 }
	 public void startCountdown()
	 {
		 setZahl(sek);
		 task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
		 {
			 public void run() 
			 {
				 if(zahl == 1) 
				 {
	                 Players.remove(name);
	                 Bukkit.getScheduler().cancelTask(task);
	             }
				 if(zahl > 1)
				 {
					 ZahlMinus();
				 }
			 }
		 }, 20, 20);
	 }
	 
	 @EventHandler
	 public void close(InventoryCloseEvent e)
	 {
		 if(e.getInventory().getName().equals("§b" + invname))
		 {
			 Players.add(name);
       	  	 startCountdown();
		 }
	 }
	 public void ZahlMinus()
	 {
		 zahl--;
	 }
	 public void setZahl(int sec)
	 {
		 zahl = sec;
	 }
	 public void loadConfig()
         {
		getConfig().options().copyDefaults(true);
		saveConfig();
	 }
}
