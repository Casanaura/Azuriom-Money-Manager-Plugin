package net.zibuu.credits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditsCommand implements CommandExecutor {
    private final Connection connection;

    public CreditsCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be performed by a player.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        try {
            // Consulta la base de datos para obtener el valor de "money" del jugador
            PreparedStatement statement = connection.prepareStatement("SELECT money FROM users WHERE name = ?");
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int money = resultSet.getInt("money");
                sender.sendMessage("You have " + money + " credits on your account.");
            } else {
                sender.sendMessage("Your username is not registered on the website.");
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
