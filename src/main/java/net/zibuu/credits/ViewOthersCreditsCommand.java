package net.zibuu.credits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewOthersCreditsCommand implements CommandExecutor {
    private final Connection connection;

    public ViewOthersCreditsCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage("You must specify a username");
                return true;
            }
            String targetPlayerName = args[0];
            try {
                // Consulta la base de datos para obtener el valor de "money" del jugador objetivo
                PreparedStatement statement = connection.prepareStatement("SELECT money FROM users WHERE name = ?");
                statement.setString(1, targetPlayerName);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int money = resultSet.getInt("money");
                    sender.sendMessage("The player " + targetPlayerName + " has " + money + " credits.");
                } else {
                    sender.sendMessage("This player is not registered on the website");
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage("An error occurred while performing this command, check the console for more information");
            }
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        if (!player.hasPermission("zibuu.credits.viewothers")) {
            sender.sendMessage("You don't have permissions to perform this command");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("You must specify a username");
            return true;
        }

        String targetPlayerName = args[0];

        try {
            // Consulta la base de datos para obtener el valor de "money" del jugador objetivo
            PreparedStatement statement = connection.prepareStatement("SELECT money FROM users WHERE name = ?");
            statement.setString(1, targetPlayerName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int money = resultSet.getInt("money");
                sender.sendMessage("The player " + targetPlayerName + " has " + money + " credits.");
            } else {
                sender.sendMessage("This player is not registered on the website");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("An error occurred while performing this command, check the console for more information");
        }
        return true;
    }
}
