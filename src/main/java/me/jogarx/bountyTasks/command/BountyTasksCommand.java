package me.jogarx.bountyTasks.command;

import me.jogarx.bountyTasks.BountyTasks;
import me.jogarx.bountyTasks.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BountyTasksCommand implements CommandExecutor {

    private final BountyTasks plugin;

    public BountyTasksCommand(BountyTasks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        LanguageManager language = plugin.getLanguageManager();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("bountytasks.reload")) {
                sender.sendMessage(
                        language.get("messages.no_permission")
                );
                return true;
            }

            plugin.reloadPlugin();

            sender.sendMessage(
                    plugin.getLanguageManager().get("messages.reload")
            );

            return true;
        }


        if (!(sender instanceof Player player)) {
            sender.sendMessage( language.get("messages.only_player"));
            return true;
        }

        Inventory inventory = plugin.getServer().createInventory(
                null,
                27,
                language.get("menu.main")
        );

        // Abrir tareas
        ItemStack openTasks = createItem(
                Material.BOOK,
                language.get("menu.open_tasks"),
                List.of(
                        language.get("menu.open_tasks_description"),
                        "",
                        language.get("menu.open_tasks_click")
                )
        );

        // Completar tareas
        ItemStack completeTasks = createItem(
                Material.LECTERN,
                language.get("menu.complete_tasks"),
                List.of(
                        language.get("menu.complete_tasks_description"),
                        language.get("menu.complete_tasks_description_2"),
                        "",
                        language.get("menu.complete_tasks_click")
                )
        );

        // Cerrar
        ItemStack close = createItem(
                Material.ARROW,
                language.get("menu.close"),
                List.of(
                        language.get("menu.close_description")
                )
        );

        // Posiciones
        inventory.setItem(12, openTasks);
        inventory.setItem(14, completeTasks);
        inventory.setItem(22, close);


        player.openInventory(inventory);

        return true;
    }

    private ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

}
