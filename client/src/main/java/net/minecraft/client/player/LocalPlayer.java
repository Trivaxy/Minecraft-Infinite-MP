package net.minecraft.client.player;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.commands.ClientCommands;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gamemode.SpectatorMode;
import net.minecraft.client.gamemode.SurvivalMode;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.client.gui.screens.container.inventory.RecipeScreen;
import net.minecraft.client.gui.screens.container.tileentity.AnvilScreen;
import net.minecraft.client.gui.screens.container.tileentity.BarrelScreen;
import net.minecraft.client.gui.screens.container.tileentity.BeaconScreen;
import net.minecraft.client.gui.screens.container.tileentity.ChestScreen;
import net.minecraft.client.gui.screens.container.tileentity.CommandScreen;
import net.minecraft.client.gui.screens.container.tileentity.CraftingScreen;
import net.minecraft.client.gui.screens.container.tileentity.GeneratorScreen;
import net.minecraft.client.gui.screens.container.tileentity.GrindstoneScreen;
import net.minecraft.client.gui.screens.container.tileentity.SignScreen;
import net.minecraft.client.gui.screens.container.tileentity.SmelterScreen;
import net.minecraft.client.gui.screens.container.tileentity.StonecutterScreen;
import net.minecraft.client.gui.screens.game.BookScreen;
import net.minecraft.client.gui.screens.game.PaintingScreen;
import net.minecraft.client.gui.screens.title.CreditsScreen;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.particle.effect.SoulParticle;
import net.minecraft.client.particle.special.PickupAnimation;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandHolder;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.smelters.BlastFurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.block.tileentity.smelters.HellforgeEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.food.FoodItem;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.Achievement;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class LocalPlayer extends Player implements CommandSource {
    public PlayerController controller;
    public Minecraft mc;
    public String lastDeath;
    private List<Biome> biomesExplored = new ArrayList<>();
    private List<Biome> biomesExploredHell = new ArrayList<>();
    private List<Biome> biomesExploredHeaven = new ArrayList<>();
    public float prevArmYaw;
    public float armYaw;
    public float prevArmPitch;
    public float armPitch;

    public LocalPlayer(Minecraft mc, World world, Session usr) {
        super(world);
        this.mc = mc;
        if (usr != null && usr.username != null && usr.username.length() > 0 && this.skinUrl == null && !this.foundSkin) {
            this.username = usr.username;
            this.updateSkin();
        }

        this.username = usr.username;
    }

    @Override
    public void addStat(Stat stat, int amount) {
        if (stat != null) {
            if (stat.isAchievement()) {
                Achievement achievement = (Achievement)stat;
                if (!this.mc.statistics.hasAchievementUnlocked(achievement)) {
                    this.mc.HUD.achievement.queueAchievement(achievement);
                    if (achievement.getSpecial()) {
                        this.score += 1000;
                    } else if (achievement.getGoal()) {
                        this.score += 500;
                    } else {
                        this.score += 100;
                    }
                }
            }

            this.mc.statistics.writeStat(stat, amount);
        }
    }

    @Override
    public void removeStat(Stat stat) {
        if (stat != null) {
            this.mc.statistics.removeStat(stat);
        }
    }

    @Override
    public void mobTick() {
        this.prevNauseaTime = this.nauseaTime;
        if (this.hasEffect(EffectList.confusion) && this.getEffect(EffectList.confusion).getPotionDuration() > 60) {
            this.nauseaTime += 0.006666667F;
            if (this.nauseaTime > 1.0F) {
                this.nauseaTime = 1.0F;
            }
        } else if (this.isInMaterial(MaterialList.water) && OptionValues.fovEffects) {
            this.nauseaTime += 0.006666667F;
            if (this.nauseaTime > 0.1F) {
                this.nauseaTime = 0.1F;
            }
        } else {
            if (this.nauseaTime > 0.0F) {
                this.nauseaTime -= 0.05F;
            }

            if (this.nauseaTime < 0.0F) {
                this.nauseaTime = 0.0F;
            }
        }

        if (!this.isPlayerSleeping()) {
            this.controller.detectInputs(this);
        }

        if (!this.sitting && !this.sleeping) {
            this.moveStrafing = this.controller.moveStrafe;
            this.moveForward = this.controller.moveForward;
            this.jumping = this.controller.jump;
        } else {
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.jumping = false;
        }

        this.prevArmYaw = this.armYaw;
        this.prevArmPitch = this.armPitch;
        this.armPitch = (float)(this.armPitch + (this.pitch - this.armPitch) * 0.5);
        this.armYaw = (float)(this.armYaw + (this.yaw - this.armYaw) * 0.5);
        this.handleSprinting();
        if (!this.mc.statistics.hasAchievementUnlocked(AchievementList.getHard)
            && !(this.mc.gamemode instanceof CreativeMode)
            && !(this.mc.gamemode instanceof SpectatorMode)) {
            this.mc.HUD.achievement.queueHint(AchievementList.getHard);
        }

        this.updateBiomesExplored();
        super.mobTick();
    }

    private void handleSprinting() {
        if (this.isSprinting() && this.mc.gamemode instanceof SurvivalMode && !this.hasEffect(EffectList.stamina) && !this.properties.getHunger()) {
            this.stamina--;
            if (this.jumping) {
                this.stamina--;
            }
        } else if (this.stamina < this.maxStamina) {
            this.stamina += 2;
        }

        if (this.stamina <= 0
            || this.getFoodStats().getHunger() < 6.0F
            || this.isSprinting() && this.controller.moveForward < 0.8F
            || this.crawling
            || this.sneaking) {
            this.setSprinting(false);
            this.setCanSprint(false);
        }

        if (this.stamina >= this.maxStamina) {
            this.setCanSprint(true);
        }
    }

    @Override
    public void switchDimension(int dim, boolean forced) {
        if (forced) {
            this.mc.switchDimension(dim);
        }

        if (this.posY < 0.0) {
            this.mountEntity(null);
            if (this.dimension == 0) {
                this.mc.switchDimension(1);
            } else if (this.dimension == 2) {
                this.mc.switchDimension(0);
            }
        } else if (this.posY > this.world.getWorldHeight() && this.dimension == 1) {
            this.mountEntity(null);
            this.mc.switchDimension(0);
        } else if (dim == 2) {
            this.mc.switchDimension(dim);
        } else if (this.canClimb(BlockList.endPortal)) {
            this.mountEntity(null);
            if (this.dimension == 3) {
                if (this.mc.statistics.hasAchievementUnlocked(AchievementList.finalBoss)) {
                    this.mc.setScreen(new CreditsScreen(true));
                }

                this.mc.switchDimension(0);
            } else {
                this.mc.switchDimension(3);
            }
        }
    }

    @Override
    public void onDeath(DamageSource ds) {
        if (!this.world.isOnline) {
            this.addChatMessage(ds.getDeathMsg(this));
        }

        this.lastDeath = ds.getDeathMsg(this);
        super.onDeath(ds);
    }

    @Override
    public void resetKeys() {
        this.controller.resetKeyState();
    }

    @Override
    public int isLit() {
        return OptionValues.dynamicLighting ? super.isLit() : 0;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("Score", this.score);
        tag.setInteger("Stamina", this.stamina);
        tag.setTag("Inventory", this.inventory.saveContents(new ListTag<>()));
        tag.setTag("InventoryGlobal", this.inventoryChest.saveContents(new ListTag<>()));
        if (this.inventory.accessorySlots[2] != null
            && this.inventory.accessorySlots[2].hasTag()
            && (this.inventory.accessorySlots[2].getItem() == ItemList.backpack || this.inventory.accessorySlots[2].getItem() == ItemList.quiver)) {
            ((PlayerContainer)this.container).saveContents(this.inventory.accessorySlots[2]);
        }

        ListTag<CompoundTag> biomeTagList = new ListTag<>();

        for (int biom = 0; biom < this.biomesExplored.size(); biom++) {
            if (this.biomesExplored.get(biom) != null) {
                CompoundTag mainTag = new CompoundTag();
                mainTag.setByte("ID", (byte)this.biomesExplored.get(biom).id);
                biomeTagList.add(mainTag);
            }
        }

        ListTag<CompoundTag> hellBiomesList = new ListTag<>();

        for (int var7 = 0; var7 < this.biomesExploredHell.size(); var7++) {
            if (this.biomesExploredHell.get(var7) != null) {
                CompoundTag mainTag = new CompoundTag();
                mainTag.setByte("ID", (byte)this.biomesExploredHell.get(var7).id);
                hellBiomesList.add(mainTag);
            }
        }

        ListTag<CompoundTag> skyBiomesList = new ListTag<>();

        for (int var8 = 0; var8 < this.biomesExploredHeaven.size(); var8++) {
            if (this.biomesExploredHeaven.get(var8) != null) {
                CompoundTag mainTag = new CompoundTag();
                mainTag.setByte("ID", (byte)this.biomesExploredHeaven.get(var8).id);
                skyBiomesList.add(mainTag);
            }
        }

        tag.setTag("Biomes", biomeTagList);
        tag.setTag("BiomesHell", hellBiomesList);
        tag.setTag("BiomesHeaven", skyBiomesList);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.score = tag.getInteger("Score");
        this.stamina = tag.getInteger("Stamina");
        ListTag<? extends Tag> inventoryTag = tag.getTagList("Inventory");
        ListTag<? extends Tag> endChestTag = tag.getTagList("InventoryGlobal");
        this.inventory.loadContents(inventoryTag);
        this.inventoryChest.loadContents(endChestTag);
        if (this.crawling) {
            this.setCrawling(this.crawling);
        }

        if (this.inventory.accessorySlots[2] != null
            && this.inventory.accessorySlots[2].hasTag()
            && (this.inventory.accessorySlots[2].getItem() == ItemList.backpack || this.inventory.accessorySlots[2].getItem() == ItemList.quiver)) {
            ((PlayerContainer)this.container).loadContents(this.inventory.accessorySlots[2]);
        }

        ListTag<? extends Tag> biomesList = tag.getTagList("Biomes");

        for (int biom = 0; biom < biomesList.size(); biom++) {
            CompoundTag biomeTag = (CompoundTag)biomesList.get(biom);
            int id = biomeTag.getByte("ID");
            if (!this.biomesExplored.contains(BiomeList.biomes[id])) {
                this.biomesExplored.add(BiomeList.biomes[id]);
            }
        }

        ListTag<? extends Tag> hellBiomesList = tag.getTagList("BiomesHell");

        for (int var10 = 0; var10 < hellBiomesList.size(); var10++) {
            CompoundTag biomeTag = (CompoundTag)hellBiomesList.get(var10);
            int id = biomeTag.getByte("ID");
            if (!this.biomesExploredHell.contains(BiomeList.biomes[id])) {
                this.biomesExploredHell.add(BiomeList.biomes[id]);
            }
        }

        ListTag<? extends Tag> skyBiomesList = tag.getTagList("BiomesHeaven");

        for (int var11 = 0; var11 < skyBiomesList.size(); var11++) {
            CompoundTag biomeTag = (CompoundTag)skyBiomesList.get(var11);
            int id = biomeTag.getByte("ID");
            if (!this.biomesExploredHeaven.contains(BiomeList.biomes[id])) {
                this.biomesExploredHeaven.add(BiomeList.biomes[id]);
            }
        }
    }

    @Override
    public void onFoodEaten() {
        int validFoodCount = 0;
        int totalFoodCount = 0;

        for (int index = 4096; index < 10240; index++) {
            if (ItemList.items[index] instanceof FoodItem) {
                totalFoodCount++;
                if (this.mc.statistics.getStatCount(StatList.useStats[index]) >= 1) {
                    validFoodCount++;
                }
            }
        }

        if (validFoodCount >= totalFoodCount) {
            this.triggerAchievement(AchievementList.diet);
        }
    }

    protected void updateBiomesExplored() {
        Biome currentBiome = this.world.getBiomeAt(MathHelper.floor(this.posX), MathHelper.floor(this.posZ));
        if (!this.biomesExplored.contains(currentBiome) && currentBiome.climateBiome) {
            this.biomesExplored.add(currentBiome);
        }

        if (!this.biomesExploredHell.contains(currentBiome) && currentBiome instanceof HellBiome) {
            this.biomesExploredHell.add(currentBiome);
        }

        if (!this.biomesExploredHeaven.contains(currentBiome) && currentBiome instanceof SkyBiome) {
            this.biomesExploredHeaven.add(currentBiome);
        }

        if (this.biomesExplored.size() >= BiomeList.climateBiomes.length) {
            this.triggerAchievement(AchievementList.adventure);
        }

        if (this.biomesExploredHell.size() >= 8) {
            this.triggerAchievement(AchievementList.adventure2);
        }

        if (this.biomesExploredHeaven.size() >= 8) {
            this.triggerAchievement(AchievementList.adventure3);
        }
    }

    @Override
    public void displayGUIChest(Inventory container, boolean locked) {
        this.mc.setScreen(new ChestScreen(this.inventory, container, locked));
        container.open();
        this.addStat(StatList.chestUse, 1);
    }

    @Override
    public void displayGUIEditSign(SignEntity sign) {
        this.mc.setScreen(new SignScreen(sign));
        this.addStat(StatList.signUse, 1);
    }

    @Override
    public void displayGUIEditCommand(CommandBlockEntity cmdBlock) {
        if (this.properties.gamemode == 1) {
            this.mc.setScreen(new CommandScreen(cmdBlock));
            this.addStat(StatList.signUse, 1);
        }
    }

    @Override
    public void displayWorkbenchGUI(int x, int y, int z, boolean portable) {
        this.mc.setScreen(new CraftingScreen(this.inventory, this.world, x, y, z, portable));
        if (!portable) {
            this.addStat(StatList.benchUse, 1);
        }
    }

    @Override
    public void displayGUIGrindstone(int x, int y, int z) {
        this.mc.setScreen(new GrindstoneScreen(this.inventory, this.world, x, y, z));
        this.addStat(StatList.grindstoneUse, 1);
    }

    @Override
    public void displayStonecutterGUI(int x, int y, int z) {
        this.mc.setScreen(new StonecutterScreen(this.inventory, this.world, x, y, z));
        this.addStat(StatList.stonecutterUse, 1);
    }

    @Override
    public void displayGUIFurnace(FurnaceEntity furnace) {
        this.mc.setScreen(new SmelterScreen(this.inventory, furnace));
        if (furnace instanceof BlastFurnaceEntity) {
            this.addStat(StatList.blastUse, 1);
        } else {
            this.addStat(StatList.furnaceUse, 1);
        }
    }

    @Override
    public void displayGUISmoker(SmokerEntity smoker) {
        this.mc.setScreen(new SmelterScreen(this.inventory, smoker));
        this.addStat(StatList.smokerUse, 1);
    }

    @Override
    public void displayGUICrafter(CrafterEntity crafter) {
        this.mc.setScreen(new CraftingScreen(this.inventory, crafter.inventory, crafter.craftResult, this.world));
        this.addStat(StatList.crafterUse, 1);
    }

    @Override
    public void displayGUIHellforge(HellforgeEntity hellforge) {
        this.mc.setScreen(new SmelterScreen(this.inventory, hellforge));
        this.addStat(StatList.hellforgeUse, 1);
    }

    @Override
    public void displayGUIPainting(int xCoord, int yCoord, int zCoord, byte type) {
        this.mc.setScreen(new PaintingScreen(xCoord, yCoord, zCoord, type));
    }

    @Override
    public void displayGUIBarrel(Inventory container) {
        this.mc.setScreen(new BarrelScreen(this.inventory, container, true));
        this.addStat(StatList.barrelUse, 1);
    }

    @Override
    public void displayGUIVacuum(Inventory container) {
        this.mc.setScreen(new BarrelScreen(this.inventory, container, false));
        this.addStat(StatList.vacuumUse, 1);
    }

    @Override
    public void displayGUIDispenser(Inventory container) {
        this.mc.setScreen(new BarrelScreen(this.inventory, container, false));
        this.addStat(StatList.dispenserUse, 1);
    }

    @Override
    public void displayGUIBookshelf(Inventory container) {
        this.mc.setScreen(new ChestScreen(this.inventory, container));
        this.addStat(StatList.shelfUse, 1);
    }

    @Override
    public void displayGUIGenerator(GeneratorEntity generator) {
        this.mc.setScreen(new GeneratorScreen(this.inventory, generator));
        this.addStat(StatList.generatorUse, 1);
    }

    @Override
    public void displayGUIBeacon(BeaconEntity beacon) {
        this.mc.setScreen(new BeaconScreen(this.inventory, beacon));
        beacon.open();
        this.addStat(StatList.beaconUse, 1);
    }

    @Override
    public void displayGUIBook(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ItemList.writtenBook) {
            this.mc.setScreen(new BookScreen(this, stack, false));
        } else if (item == ItemList.bookAndQuill) {
            this.mc.setScreen(new BookScreen(this, stack, true));
        }
    }

    @Override
    public void displayGUIRecipe() {
        if (this.mc.currentScreen instanceof ContainerScreen && this.openContainer == this.container) {
            ((ContainerScreen)this.mc.currentScreen).keepContainerOpen();
        }

        this.mc.setScreen(new RecipeScreen(this.inventory));
    }

    @Override
    public void displayGUIAnvil(int x, int y, int z) {
        this.mc.setScreen(new AnvilScreen(this.inventory, this.world, x, y, z));
        this.addStat(StatList.anvilUse, 1);
    }

    @Override
    public void displayGUIEnderChest(Inventory container, Inventory other) {
        this.mc.setScreen(new ChestScreen(this.inventory, container, other));
        other.open();
        this.addStat(StatList.endChestUse, 1);
    }

    @Override
    public ItemStack getCurrentEquippedItem() {
        return this.inventory.getCurrentItem();
    }

    @Override
    public void spawnCritParticles(Entity target, ParticleType type, int amount) {
        if (amount > 50) {
            amount = 50;
        }

        for (int numberOfParticles = 0; numberOfParticles < amount; numberOfParticles++) {
            double sizeX = this.rand.nextFloat() * 2.0F - 1.0F;
            double sizeY = this.rand.nextFloat() * 2.0F - 1.0F;
            double sizeZ = this.rand.nextFloat() * 2.0F - 1.0F;
            if (sizeX * sizeX + sizeY * sizeY + sizeZ * sizeZ <= 1.0) {
                double newX = target.posX + sizeX * target.width / 4.0;
                double newY = target.boundingBox.minY + target.height / 2.0F + sizeY * target.height / 4.0;
                double newZ = target.posZ + sizeZ * target.width / 4.0;
                this.world.spawnParticle(type, newX, newY, newZ, sizeX, sizeY + 0.2, sizeZ);
            }
        }
    }

    @Override
    public void spawnSoulParticles(Entity target) {
        this.mc.particleRenderer.addParticle(new SoulParticle(this.mc.world, target));
    }

    @Override
    public void onItemPickup(Entity target) {
        this.mc.particleRenderer.addParticle(new PickupAnimation(this.mc.world, target, this, -0.5F));
        if (target instanceof DroppedItem
            && this.inventory.getCurrentItem() != null
            && ((DroppedItem)target).item.getItem() == this.inventory.getCurrentItem().getItem()) {
            this.mc.gameRenderer.itemRenderer.resetEquippedProgress();
        }
    }

    @Override
    public void closeScreen() {
        super.closeScreen();
        this.mc.setScreen(null);
    }

    @Override
    public void updateSkin() {
        if (this.skinUrl == null && !this.foundSkin) {
            new SkinDownloader(this).start();
        }

        if (this.isSlim) {
            this.texture = "/mob/player/alex.png";
        }
    }

    @Override
    public void sendChatMessage(String message) {
        if (this.mc.HUD.chatOverlay.getChatHistory().isEmpty()
            || !this.mc.HUD.chatOverlay.getChatHistory().get(this.mc.HUD.chatOverlay.getChatHistory().size() - 1).equals(message)) {
            this.mc.HUD.chatOverlay.getChatHistory().add(message);
        }

        if (message.startsWith("/")) {
            ClientCommands.instance.runCommand(new CommandHolder(message.substring(1).trim(), this), this.mc);
        } else {
            this.mc.HUD.chatOverlay.addChatMessage("§e<" + this.username + "> " + "§f" + message);
        }
    }

    @Override
    public void addChatMessage(String msg) {
        this.mc.HUD.chatOverlay.addChatMessage(msg);
    }

    @Override
    public void setHealth(int amount) {
        int health = this.health - amount;
        if (health <= 0) {
            this.health = amount;
            if (health < 0) {
                this.damage = this.invulnerabilityFrames / 2;
            }
        } else {
            this.damageTaken = health;
            this.prevHealth = this.health;
            this.damage = this.invulnerabilityFrames;
            this.applyDamageMultiplier(DamageSource.generic, health);
            this.hurtTime = this.hurtDuration = 10;
        }
    }

    public void respawnPlayer() {
        this.mc.respawn(false);
    }

    public float getFOVMultiplier() {
        float multiplier = 1.0F;
        if (!OptionValues.fovEffects) {
            return multiplier;
        }

        if (this.flying) {
            multiplier *= 1.1F;
        }

        multiplier *= (this.landSpeed * this.applySpeedModifier() / this.prevLandSpeed + 1.0F) / 2.0F;
        if (this.isUsingItem() && (this.getItemInUse().getItem() instanceof Crossbow || this.getItemInUse().itemID == ItemList.trident.id)) {
            int useDuration = this.getItemUseDur();
            float zoom = useDuration / 20.0F;
            if (zoom > 1.0F) {
                zoom = 1.0F;
            } else {
                zoom *= zoom;
            }

            multiplier *= 1.0F - zoom * 0.15F;
        }

        return multiplier;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void print(String text) {
        this.mc.HUD.chatOverlay.addChatMessage(text);
    }

    @Override
    public Command.UserType getType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public boolean canRunCommand(Command command) {
        return this.world.getSettings().getCheats() || command.requiredUserType() == Command.UserType.USER;
    }
}
