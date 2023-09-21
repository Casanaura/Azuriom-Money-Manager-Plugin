package net.zibuu.credits;

// Spigot API
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

// MySQL Connector
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ZibuuCredits extends JavaPlugin {
    FileConfiguration config = getConfig();
    private Connection connection;

    /*
        Aquí comienza nuestro código
        que se accionará al momento de
        habilitar nuestro plug-in
    */
    @Override
    public void onEnable() {

        // Obteniendo configuraciones del plug-in del archivo config.yml
        config.options().copyDefaults(true);
        saveConfig();

        // Configura la conexión a la base de datos
        if (setupDatabase()) {
            getLogger().info("Conexión a la base de datos exitosa.");
        }
        else {
            getLogger().severe("Error al conectar a la base de datos.");
        }

        // Comando: "/credits"
        PluginCommand creditsCommand = getCommand("credits");
        if (creditsCommand != null) {
            creditsCommand.setExecutor(new CreditsCommand(connection));
        }
        else {
            getLogger().warning("No se pudo registrar el comando /credits.");
        }

        // Comando: "/creditsadd playerName amount"
        PluginCommand addCreditsCommand = getCommand("creditsadd");
        if (addCreditsCommand != null) {
            addCreditsCommand.setExecutor(new AddCreditsCommand(connection));
        }
        else {
            getLogger().warning("No se pudo registrar el comando /credits add.");
        }

        // Comando: "/creditsrem playerName amount"
        PluginCommand removeCreditsCommand = getCommand("creditsrem");
        if (removeCreditsCommand != null) {
            removeCreditsCommand.setExecutor(new RemoveCreditsCommand(connection));
        }
        else {
            getLogger().warning("No se pudo registrar el comando /creditsrem.");
        }

        // Comando: "/creditsset playerName amount"
        PluginCommand setCreditsCommand = getCommand("creditsset");
        if (setCreditsCommand != null) {
            setCreditsCommand.setExecutor(new SetCreditsCommand(connection));
        }
        else {
            getLogger().warning("No se pudo registrar el comando /creditsset.");
        }

        // Comando: "/creditsview playerName"
        PluginCommand viewOthersCreditsCommand = getCommand("creditsview");
        if (viewOthersCreditsCommand != null) {
            viewOthersCreditsCommand.setExecutor(new ViewOthersCreditsCommand(connection));
        }
        else {
            getLogger().warning("No se pudo registrar el comando /creditsview.");
        }
    }

    /*
        Aquí comienza nuestro código
        que se activará al momento de deshabilitar
        nuestro plug-in,
    */
    @Override
    public void onDisable() {
        // Cierra la conexión a la base de datos al desactivar el plugin si es necesario
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Aquí comienza nuestro código para configurar
        la conexión a nuestra base de datos MySQL
    */
    private boolean setupDatabase() {
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.name");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        try {
            // Carga el controlador JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establece la conexión a la base de datos
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            return true;
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
