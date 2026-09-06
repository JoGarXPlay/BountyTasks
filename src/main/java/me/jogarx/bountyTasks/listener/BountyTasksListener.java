package me.jogarx.bountyTasks.listener;

import me.jogarx.bountyTasks.BountyTasks;
import me.jogarx.bountyTasks.bounty.Bounty;
import me.jogarx.bountyTasks.bounty.BountyBook;
import me.jogarx.bountyTasks.bounty.BountyReward;
import me.jogarx.bountyTasks.bounty.BountyValidator;
import me.jogarx.bountyTasks.command.BountyTasksCommand;
import me.jogarx.bountyTasks.language.LanguageManager;
import me.jogarx.bountyTasks.menu.BountyListMenu;
import me.jogarx.bountyTasks.menu.CompleteBountyMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class BountyTasksListener implements Listener {

    private final BountyTasks plugin;

    public BountyTasksListener(BountyTasks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        LanguageManager language =
                plugin.getLanguageManager();

        String title = event.getView().getTitle();

        // =========================
        // MENÚ PRINCIPAL
        // =========================
        if (title.equals(language.get("menu.main"))) {

            event.setCancelled(true);

            if (event.getRawSlot() >=
                    event.getView().getTopInventory().getSize()) {
                return;
            }

            switch (event.getRawSlot()) {

                case 12 -> openTasksMenu(player);

                case 14 -> completeTasksMenu(player);

                case 22 -> player.closeInventory();
            }

            return;
        }

        // =========================
        // LISTA DE TAREAS
        // =========================
        if (title.equals(language.get("menu.open_tasks"))) {

            event.setCancelled(true);

            if (event.getRawSlot() >=
                    event.getView().getTopInventory().getSize()) {
                return;
            }

            // Volver al menú principal
            if (event.getRawSlot() == 22) {

                player.closeInventory();

                BountyTasksCommand command =
                        new BountyTasksCommand(plugin);

                command.onCommand(
                        player,
                        null,
                        "bountytasks",
                        new String[0]
                );

                return;
            }

            ItemStack clickedItem =
                    event.getCurrentItem();

            if (clickedItem == null ||
                    clickedItem.getType() == Material.AIR) {
                return;
            }

            BountyBook bountyBook =
                    new BountyBook(plugin);

            String bountyId =
                    bountyBook.getBountyId(clickedItem);

            if (bountyId == null) {
                return;
            }

            Bounty bounty =
                    plugin.getBountyManager()
                            .getBounty(bountyId);

            if (bounty == null) {
                return;
            }

            bountyBook.give(player, bounty);

            player.closeInventory();

            player.sendMessage(
                    language.get(
                            "messages.task_accepted",
                            Map.of(
                                    "task",
                                    bounty.getName()
                            )
                    )
            );

            return;
        }

        // =========================
        // LISTA DE TAREAS DEL JUGADOR
        // =========================
        if (title.equals(
                language.get("menu.complete_tasks"))) {

            event.setCancelled(true);

            if (event.getRawSlot() >=
                    event.getView().getTopInventory().getSize()) {
                return;
            }

            // Volver al menú principal
            if (event.getRawSlot() == 22) {

                player.closeInventory();

                BountyTasksCommand command =
                        new BountyTasksCommand(plugin);

                command.onCommand(
                        player,
                        null,
                        "bountytasks",
                        new String[0]
                );

                return;
            }

            ItemStack clickedItem =
                    event.getCurrentItem();

            if (clickedItem == null ||
                    clickedItem.getType() == Material.AIR) {
                return;
            }

            BountyBook bountyBook =
                    new BountyBook(plugin);

            String bountyId =
                    bountyBook.getBountyId(clickedItem);

            if (bountyId == null) {
                return;
            }

            Bounty bounty =
                    plugin.getBountyManager()
                            .getBounty(bountyId);

            if (bounty == null) {
                return;
            }

            CompleteBountyMenu menu =
                    new CompleteBountyMenu(plugin);

            menu.open(player, bounty);

            return;
        }

        // =========================
        // DETALLE DE TAREA
        // =========================
        if (title.equals(
                language.get("task.complete"))) {

            event.setCancelled(true);

            if (event.getRawSlot() >=
                    event.getView().getTopInventory().getSize()) {
                return;
            }

            // Volver a la lista de tareas
            if (event.getRawSlot() == 22) {

                completeTasksMenu(player);

                return;
            }

            // Solo nos interesa el botón completar
            if (event.getRawSlot() != 15) {
                return;
            }

            ItemStack clickedItem =
                    event.getCurrentItem();

            if (clickedItem == null ||
                    clickedItem.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta =
                    clickedItem.getItemMeta();

            if (meta == null) {
                return;
            }

            String bountyId =
                    meta.getPersistentDataContainer().get(
                            new NamespacedKey(
                                    plugin,
                                    "bounty_id"
                            ),
                            PersistentDataType.STRING
                    );

            if (bountyId == null) {
                return;
            }

            Bounty bounty =
                    plugin.getBountyManager()
                            .getBounty(bountyId);

            if (bounty == null) {
                return;
            }

            BountyValidator validator =
                    new BountyValidator();

            if (!validator.hasRequirements(
                    player,
                    bounty
            )) {

                player.sendMessage(
                        language.get(
                                "messages.missing_requirements"
                        )
                );

                return;
            }

            BountyBook bountyBook =
                    new BountyBook(plugin);

            if (!bountyBook.remove(
                    player,
                    bounty
            )) {

                player.sendMessage(
                        language.get(
                                "messages.book_not_found"
                        )
                );

                return;
            }

            validator.removeRequirements(
                    player,
                    bounty
            );

            for (BountyReward reward :
                    bounty.getRewards()) {

                ItemStack rewardItem =
                        new ItemStack(
                                reward.getMaterial(),
                                reward.getAmount()
                        );

                Map<Integer, ItemStack> leftovers =
                        player.getInventory().addItem(
                                rewardItem
                        );

                for (ItemStack leftover :
                        leftovers.values()) {

                    player.getWorld().dropItemNaturally(
                            player.getLocation(),
                            leftover
                    );
                }
            }

            player.closeInventory();

            player.sendMessage(
                    language.get(
                            "messages.task_completed",
                            Map.of(
                                    "task",
                                    bounty.getName()
                            )
                    )
            );

            return;
        }
    }

    private void openTasksMenu(Player player) {

        BountyListMenu menu =
                new BountyListMenu(plugin);

        menu.open(player);
    }

    private void completeTasksMenu(Player player) {

        LanguageManager language =
                plugin.getLanguageManager();

        Inventory inventory =
                plugin.getServer().createInventory(
                        null,
                        27,
                        language.get(
                                "menu.complete_tasks"
                        )
                );

        BountyBook bountyBook =
                new BountyBook(plugin);

        int slot = 10;

        for (ItemStack item :
                player.getInventory().getContents()) {

            if (item == null ||
                    item.getType() == Material.AIR) {
                continue;
            }

            String bountyId =
                    bountyBook.getBountyId(item);

            if (bountyId == null) {
                continue;
            }

            inventory.setItem(
                    slot,
                    item.clone()
            );

            slot++;

            if (slot == 17) {
                break;
            }
        }

        // Botón volver
        ItemStack back =
                new ItemStack(Material.ARROW);

        ItemMeta backMeta =
                back.getItemMeta();

        if (backMeta != null) {

            backMeta.setDisplayName(
                    language.get("menu.back")
            );

            backMeta.setLore(List.of(
                    language.get(
                            "menu.back_description"
                    )
            ));

            back.setItemMeta(backMeta);
        }

        inventory.setItem(22, back);

        player.openInventory(inventory);
    }
}