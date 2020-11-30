package uk.jadestudios.tieredhandouts;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;


public final class tieredhandouts extends JavaPlugin {

    private Economy economy;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null){
            economy = null;
            Bukkit.shutdown();
        }else{
            economy = economyProvider.getProvider();
        }
        this.saveDefaultConfig();
        //TODO
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;


        if (player.getWorld().getName().equals(this.getConfig().getString("world"))) {

            switch(command.getName().toLowerCase()){
                case "run":
                    Collection<? extends Player> players = this.getServer().getOnlinePlayers();
                    for (Player s : players) {
                        if (economy.getBalance(s) < this.getConfig().getDouble("middle")) {
                            economy.depositPlayer(s, this.getConfig().getDouble("middleCost"));
                            
                        } else {
                            economy.depositPlayer(s, this.getConfig().getDouble("upperCost"));
                        };

                    }
                    player.sendMessage("Done");
            }

        } else {
            sender.sendMessage("wrong world");

        }


        return true;

    }

    @Override
    public void onDisable() {
        //TODO
    }
}
