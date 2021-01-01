package uk.jadestudios.UBImc;

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

public final class UBImc extends JavaPlugin implements Listener {

    private Economy economy;
    private List<String> worlds;
    private List<Double> threshold;
    private List<Double> ubi;
    private long time;
    private String verbose;

    /**
     * In order of what onEnable() does
     * Checks and hooks to an economy plugin
     * Saves a default config or loads a config
     * Checks the sizes of parameters
     * Starts a listener on this plugin
     */
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

        worlds = this.getConfig().getStringList("worlds");
        threshold = this.getConfig().getDoubleList("UBIthreshold");
        ubi = this.getConfig().getDoubleList("UBI");

        while (!(threshold.size() == ubi.size() - 1)){
            this.getServer().getConsoleSender().sendMessage("[UBImc] Check UBI config: Threshold should be one less than UBI");
            this.getServer().getConsoleSender().sendMessage("[UBImc] This will not halt the plugin.");
            this.getServer().getConsoleSender().sendMessage("[UBImc] Last threshold value will not be used.");
            threshold.remove(threshold.size() - 1);
        }

        time = Math.round(this.getConfig().getDouble("time")) * 60 * 20;
        verbose = this.getConfig().getString("verbose").toLowerCase();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Holds the command /runubi that only works from the console
     * /runubi calls UBIcalc()
     * @param sender Who the command sender is
     * @param command What the command is
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            switch (command.getName().toLowerCase()) {
                case "runubi":
                    Collection<? extends Player> players = this.getServer().getOnlinePlayers();
                    if (!players.isEmpty()) {
                        UBIcalc(players);
                        sender.sendMessage("[UBImc] Manual run complete.");
                    } else {
                        sender.sendMessage("[UBImc] No Players.");
                    }
            }
        }else{
            sender.sendMessage("Console command only!");
        }

        return true;

    }

    @Override
    public void onDisable() {
        //Probs some cool stuff later.
    }

    /**
     * Checks whether a players economy balance is within the UBI thresholds
     * and deposits given the tier
     * @param players A collection of players called from getOnlinePlayers()
     */
    private void UBIcalc(Collection<? extends Player> players) {
        for (Player s : players) {
            this.consoleSendMessage("[UBImc] UBI Started for " + s.getName(), verbose);
            if (s.getGameMode().equals(GameMode.SURVIVAL) && worlds.contains(s.getWorld().getName())) {

                double currentBalance = economy.getBalance(s);
                double UBIvalue = 0;

                if (currentBalance >= threshold.get(0)) {
                    economy.depositPlayer(s, ubi.get(0));
                    UBIvalue = ubi.get(0);
                } else if (currentBalance < threshold.get(threshold.size() - 1)) {
                    economy.depositPlayer(s, ubi.get(ubi.size() - 1));
                    UBIvalue = ubi.get(ubi.size() - 1);
                }

                for (int i = 0; i < threshold.size() - 1; i++) {
                    if (currentBalance < threshold.get(i) && currentBalance >= threshold.get(i + 1)) {
                        economy.depositPlayer(s, ubi.get(i + 1));
                        UBIvalue = ubi.get(i + 1);
                    }
                }
                this.consoleSendMessage("[UBImc] UBI claimed by " + s.getName() + " for " + UBIvalue, verbose);
                s.sendMessage("[UBImc] You have received $" + UBIvalue);
            }
            this.consoleSendMessage("[UBImc] UBI completed for " + s.getName(), verbose);
        }
    }

    /**
     * Method to hold UBIcalc() and gets the collection of players beforehand
     */
    public void getUBI() {
        this.consoleSendMessage("[UBImc] UBI Batch prepping", verbose);
        Collection<? extends Player> players = this.getServer().getOnlinePlayers();
        if (!players.isEmpty()) {
            this.consoleSendMessage("[UBImc] UBI Batch starting", verbose);
            UBIcalc(players);
        }

    }

    /**
     * Verbose logging
     * @param string Message
     * @param bool Verbose boolean as a string whether true/false or t/f
     */
    public void consoleSendMessage(String string, String bool) {
        if (bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("t")) {
            this.getServer().getConsoleSender().sendMessage(string);
        }
    }


    /**
     *Starts a timer for the UBI process on initial player join
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.getServer().getOnlinePlayers().size() <= 1) {
            //System.out.println("TIMER START");
            this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    getUBI();
                }
            }, time, time);
        }
    }

    /**
     *Stops all scheduled tasks on last player leave
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.getServer().getOnlinePlayers().size() <= 1) {
            //System.out.println("TIMER STOP");
            this.getServer().getScheduler().cancelTasks(this);
        }
    }


}
