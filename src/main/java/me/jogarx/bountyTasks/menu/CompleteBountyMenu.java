package me.jogarx.bountyTasks.menu;

import me.jogarx.bountyTasks.BountyTasks;
import me.jogarx.bountyTasks.bounty.Bounty;
import me.jogarx.bountyTasks.bounty.BountyRequirement;
import me.jogarx.bountyTasks.bounty.BountyReward;
import me.jogarx.bountyTasks.bounty.BountyValidator;
import me.jogarx.bountyTasks.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompleteBountyMenu {

    private final BountyTasks plugin;

    public CompleteBountyMenu(BountyTasks plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Bounty bounty) {

        LanguageManager language =
                plugin.getLanguageManager();

        Inventory inventory =
                plugin.getServer().createInventory(
                        null,
                        27,
                        language.get("task.complete")
                );

        BountyValidator validator =
                new BountyValidator();

        boolean canComplete =
                validator.hasRequirements(
                        player,
                        bounty
                );

        // =========================
        // TAREA
        // =========================

        ItemStack taskItem =
                createTaskItem(
                        bounty,
                        player,
                        canComplete,
                        language
                );

        inventory.setItem(13, taskItem);

        // =========================
        // BOTÓN COMPLETAR
        // =========================

        ItemStack complete =
                new ItemStack(Material.LIME_DYE);

        ItemMeta completeMeta =
                complete.getItemMeta();

        if (completeMeta != null) {

            completeMeta.setDisplayName(
                    language.get("task.complete")
            );

            completeMeta.setLore(List.of(
                    language.get(
                            "menu.complete_tasks_description"
                    ),
                    language.get(
                            "menu.complete_tasks_description_3"
                    )
            ));

            NamespacedKey bountyIdKey =
                    new NamespacedKey(
                            plugin,
                            "bounty_id"
                    );

            completeMeta.getPersistentDataContainer().set(
                    bountyIdKey,
                    PersistentDataType.STRING,
                    bounty.getId()
            );

            complete.setItemMeta(completeMeta);
        }

        inventory.setItem(15, complete);

        // =========================
        // BOTÓN VOLVER
        // =========================

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
                            "menu.back_description_2"
                    )
            ));

            back.setItemMeta(backMeta);
        }

        inventory.setItem(22, back);

        player.openInventory(inventory);
    }

    private ItemStack createTaskItem(
            Bounty bounty,
            Player player,
            boolean canComplete,
            LanguageManager language
    ) {

        ItemStack item =
                new ItemStack(
                        bounty.getDisplayMaterial()
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // =========================
        // NOMBRE DE LA TAREA
        // =========================

        meta.setDisplayName(
                language.get(
                        "task.name",
                        Map.of(
                                "name",
                                bounty.getName()
                        )
                )
        );

        List<String> lore =
                new ArrayList<>();

        // =========================
        // REQUISITOS
        // =========================

        lore.add(
                language.get(
                        "task.requirements"
                )
        );

        BountyValidator validator =
                new BountyValidator();

        for (BountyRequirement requirement :
                bounty.getRequirements()) {

            int current =
                    validator.countItems(
                            player,
                            requirement.getMaterial()
                    );

            String status;

            if (current >= requirement.getAmount()) {
                status = "§a✔";
            } else {
                status = "§c✘";
            }

            lore.add(
                    language.get(
                            "task.requirement_status_line",
                            Map.of(
                                    "status",
                                    status,
                                    "amount",
                                    String.valueOf(
                                            requirement.getAmount()
                                    ),
                                    "material",
                                    getMaterialName(
                                            requirement.getMaterial(),
                                            language
                                    )
                            )
                    )
            );
        }

        lore.add("");

        // =========================
        // RECOMPENSAS
        // =========================

        lore.add(
                language.get(
                        "task.rewards"
                )
        );

        for (BountyReward reward :
                bounty.getRewards()) {

            lore.add(
                    language.get(
                            "task.reward_line",
                            Map.of(
                                    "amount",
                                    String.valueOf(
                                            reward.getAmount()
                                    ),
                                    "material",
                                    getMaterialName(
                                            reward.getMaterial(),
                                            language
                                    )
                            )
                    )
            );
        }

        lore.add("");

        // =========================
        // ESTADO DE LA TAREA
        // =========================

        if (canComplete) {

            lore.add(
                    language.get(
                            "task.can_complete"
                    )
            );

        } else {

            lore.add(
                    language.get(
                            "task.cannot_complete"
                    )
            );
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private String getMaterialName(
            Material material,
            LanguageManager language
    ) {

        return language.get(
                "materials." +
                        material.name().toLowerCase()
        );
    }
}