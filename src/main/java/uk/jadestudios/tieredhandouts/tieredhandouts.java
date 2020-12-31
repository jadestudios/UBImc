package uk.jadestudios.tieredhandouts;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Collection;
import java.util.List;


public final class tieredhandouts extends JavaPlugin implements Listener {

    private Economy economy;
    private List<String> worlds;
    private List<Double> threshold;
    private List<Double> ubi;
    private long time;


    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null) {
            economy = null;
            Bukkit.shutdown();
        } else {
            economy = economyProvider.getProvider();
        }
        this.saveDefaultConfig();
        //TODO

        if (this.getConfig().isList("worlds")) {
            worlds = this.getConfig().getStringList("worlds");
        }

        //Check if threshold = ubi - 1 and actually exists
        threshold = this.getConfig().getDoubleList("UBIthreshold");
        ubi = this.getConfig().getDoubleList("UBI");
        if (!(threshold.size() == ubi.size() - 1)) {
            this.getServer().broadcastMessage("Check UBI config: Threshold should be one less than UBI");
        }

        time = Math.round(this.getConfig().getDouble("time")) * 60 * 20;
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    //UBI in a sense
    //Where people above a threshold gets x
    //Where people below gets x
    // and so on
    //Should check player world so correct account or gamemode
    //Needs timed event

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender; //Console issue


        //Needs to check if console
        //Probably depracating inline commands.
        switch (command.getName().toLowerCase()) {
            case "run":
                Collection<? extends Player> players = this.getServer().getOnlinePlayers();
                if (!players.isEmpty()) {
                    UBIcalc(players);
                } else {
                    player.sendMessage("EMPTY");
                    return false;
                }
        }
        player.sendMessage("Done");
        return true;

    }

    @Override
    public void onDisable() {
        //TODO
    }

    private void UBIcalc(Collection<? extends Player> players) {
        for (Player s : players) {
            if (s.getGameMode().equals(GameMode.SURVIVAL) && worlds.contains(s.getWorld().getName())) {
                double currentBalance = economy.getBalance(s);

                if (currentBalance >= threshold.get(0)) {
                    economy.depositPlayer(s, ubi.get(0));
                } else if (currentBalance < threshold.get(threshold.size() - 1)) {
                    economy.depositPlayer(s, ubi.get(ubi.size() - 1));
                }

                for (int i = 0; i < threshold.size() - 1; i++) {
                    if (currentBalance < threshold.get(i) && currentBalance >= threshold.get(i + 1)) {
                        economy.depositPlayer(s, ubi.get(i + 1));
                    }
                }
            }
        }
    }

    public void getUBI() {
        this.getServer().broadcastMessage("UBIget");

        Collection<? extends Player> players = this.getServer().getOnlinePlayers();
        if (!players.isEmpty()) {
            this.getServer().broadcastMessage("UBIproc");
            UBIcalc(players);
        }

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.getServer().broadcastMessage("joinEvent");
        if (this.getServer().getOnlinePlayers().size() <= 1) {
            this.getServer().broadcastMessage("joinEventProc");
            this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    getUBI();
                }
            }, time, time);
        }

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.getServer().broadcastMessage("quitEvent");
        if (this.getServer().getOnlinePlayers().size() <= 1) {
            this.getServer().broadcastMessage("quitEventProc");
            this.getServer().getScheduler().cancelTasks(this);
        }


    }


}
