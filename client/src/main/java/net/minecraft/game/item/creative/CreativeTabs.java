package net.minecraft.game.item.creative;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class CreativeTabs {
    public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
    public static final CreativeTabs tabNature = new CreativeTabs(0, "naturalBlocks") {
        @Override
        public int getIconIndex() {
            return BlockList.grass.id;
        }
    };
    public static final CreativeTabs tabBlocks = new CreativeTabs(1, "buildingBlocks") {
        @Override
        public int getIconIndex() {
            return BlockList.stoneBrick.id;
        }
    };
    public static final CreativeTabs tabColor = new CreativeTabs(2, "colors") {
        @Override
        public int getIconIndex() {
            return BlockList.cloth.id;
        }

        @Override
        public int getMetadata() {
            return 8;
        }
    };
    public static final CreativeTabs tabDecoration = new CreativeTabs(3, "decoration") {
        @Override
        public int getIconIndex() {
            return ItemList.sign.id;
        }
    };
    public static final CreativeTabs tabMachines = new CreativeTabs(4, "redstone") {
        @Override
        public int getIconIndex() {
            return BlockList.cog.id;
        }
    };
    public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search") {
        @Override
        public int getIconIndex() {
            return ItemList.compass.id;
        }
    }).setBackgroundImage("search");
    public static final CreativeTabs tabTools = new CreativeTabs(6, "tools") {
        @Override
        public int getIconIndex() {
            return ItemList.pickaxeSteel.id;
        }
    };
    public static final CreativeTabs tabCombat = new CreativeTabs(7, "combat") {
        @Override
        public int getIconIndex() {
            return ItemList.swordDiamond.id;
        }
    };
    public static final CreativeTabs tabFood = new CreativeTabs(8, "food") {
        @Override
        public int getIconIndex() {
            return ItemList.appleRed.id;
        }
    };
    public static final CreativeTabs tabMaterials = new CreativeTabs(9, "materials") {
        @Override
        public int getIconIndex() {
            return ItemList.coal.id;
        }
    };
    public static final CreativeTabs tabMisc = new CreativeTabs(10, "misc") {
        @Override
        public int getIconIndex() {
            return ItemList.spawnEgg.id;
        }
    };
    public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory") {
        @Override
        public int getIconIndex() {
            return BlockList.chest.id;
        }
    }).setBackgroundImage("survival_inv").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;
    private String bgImage = "list_items";
    private boolean hasScrollbar = true;
    private boolean drawTitle = true;

    public CreativeTabs(int windex, String str) {
        this.tabIndex = windex;
        this.tabLabel = str;
        creativeTabArray[windex] = this;
    }

    public int getTabIndex() {
        return this.tabIndex;
    }

    public String getLabel() {
        return this.tabLabel;
    }

    public String getTranslatedLabel() {
        return "container." + this.getLabel();
    }

    public Item getIcon() {
        return ItemList.items[this.getIconIndex()];
    }

    public int getIconIndex() {
        return 1;
    }

    public String getBackgroundImage() {
        return this.bgImage;
    }

    public CreativeTabs setBackgroundImage(String par1Str) {
        this.bgImage = par1Str;
        return this;
    }

    public boolean hasTitle() {
        return this.drawTitle;
    }

    public CreativeTabs setNoTitle() {
        this.drawTitle = false;
        return this;
    }

    public boolean hideInventory() {
        return this.hasScrollbar;
    }

    public CreativeTabs setNoScrollbar() {
        this.hasScrollbar = false;
        return this;
    }

    public int getTabColumn() {
        return this.tabIndex % 6;
    }

    public boolean isFirstRow() {
        return this.tabIndex < 6;
    }

    public void displayAllItems(List<ItemStack> stacks) {
        List<Block> blocks = CreativeItemsList.BLOCKS;

        for (int index = 0; index < blocks.size(); index++) {
            Block block = blocks.get(index);
            if (block != null && block.creativeCategory() == this) {
                block.getSubStacks(block.id, this, stacks);
            }
        }

        List<Item> items = CreativeItemsList.ITEMS;

        for (int index = 0; index < items.size(); index++) {
            Item item = items.get(index);
            if (item != null && item.creativeCategory() == this) {
                item.getSubItems(item.id, this, stacks);
            }
        }
    }

    public int getMetadata() {
        return 0;
    }
}
