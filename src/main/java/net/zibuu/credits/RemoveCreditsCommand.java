package net.zibuu.credits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveCreditsCommand implements CommandExecutor {
    private final Connection connection;

    public RemoveCreditsCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage("You must specify a username and a proper value.");
                return true;
            }

            String targetPlayerName = args[0];
            int amount;

            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("You must enter an integer");
                return true;
            }

            try {
                // Consulta la base de datos para verificar si el jugador objetivo existe
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
                statement.setString(1, targetPlayerName);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // El jugador objetivo existe, ahora puedes restar créditos
                    int currentMoney = resultSet.getInt("money");
                    int newMoney = Math.max(currentMoney - amount, 0);

                    // Actualiza la columna "money" para el jugador objetivo
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE users SET money = ? WHERE name = ?");
                    updateStatement.setInt(1, newMoney);
                    updateStatement.setString(2, targetPlayerName);
                    updateStatement.executeUpdate();

                    sender.sendMessage("You removed " + amount + " credits from " + targetPlayerName + ". New balance is " + newMoney);
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

        if (!player.hasPermission("zibuu.credits.remove")) {
            sender.sendMessage("You don't have permissions to perform this command");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("You must specify a username and a proper value.");
            return true;
        }

        String targetPlayerName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("You must enter an integer");
            return true;
        }

        try {
            // Consulta la base de datos para verificar si el jugador objetivo existe
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
            statement.setString(1, targetPlayerName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // El jugador objetivo existe, ahora puedes restar créditos
                int currentMoney = resultSet.getInt("money");
                int newMoney = Math.max(currentMoney - amount, 0);

                // Actualiza la columna "money" para el jugador objetivo
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE users SET money = ? WHERE name = ?");
                updateStatement.setInt(1, newMoney);
                updateStatement.setString(2, targetPlayerName);
                updateStatement.executeUpdate();

                sender.sendMessage("You removed " + amount + " credits from " + targetPlayerName + ". New balance is " + newMoney);
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
