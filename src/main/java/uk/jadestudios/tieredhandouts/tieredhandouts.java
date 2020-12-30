package uk.jadestudios.tieredhandouts;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;


public final class tieredhandouts extends JavaPlugin {

    private Economy economy;
    private List<World> worlds;
    private List<Double> threshold;
    private List<Double> ubi;


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

        if(this.getConfig().isList("worlds")) {
            worlds = (List<World>) this.getConfig().getList("worlds");
        }

        //Check if threshold = ubi - 1 and actually exists

        threshold = (List<Double>) this.getConfig().getList("UBIthreshold");
        ubi = (List<Double>) this.getConfig().getList("UBI");

    }

    //UBI in a sense
    //Where people above a threshold gets x
    //Where people below gets x
    // and so on
    //Should check player world so correct account or gamemode
    //Needs timed event

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
//
//
//        if (player.getWorld().getName().equals(this.getConfig().getString("gamemode"))) {

            switch(command.getName().toLowerCase()){
                case "run":
                    Collection<? extends Player> players = this.getServer().getOnlinePlayers();
                    for (Player s : players) {
                        if (s.getGameMode().equals(GameMode.SURVIVAL) && worlds.contains(s.getWorld())){

                            double currentBalance = economy.getBalance(s);

                            if (currentBalance >= threshold.get(0)){
                                economy.depositPlayer(s,ubi.get(0));
                            }else if (currentBalance < threshold.get(threshold.size()-1)){
                                economy.depositPlayer(s,ubi.get(ubi.size()-1));
                            }

                            for (int i = 0; i < threshold.size()-1; i++){
                                if (currentBalance < threshold.get(i) && currentBalance >= threshold.get(i+1)){
                                    economy.depositPlayer(s, ubi.get(i+1));
                                }
                            }
                    }

                    }
                    player.sendMessage("Done");
            }

//        } else {
//            sender.sendMessage("wrong world");


        return true;

    }

    @Override
    public void onDisable() {
        //TODO
    }
}
