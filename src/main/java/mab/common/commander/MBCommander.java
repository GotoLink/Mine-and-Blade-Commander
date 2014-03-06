package mab.common.commander;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import mab.common.commander.utils.MBCommanderForgeEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import mab.common.commander.block.BlockBanner;
import mab.common.commander.block.BlockItemBanner;
import mab.common.commander.block.TileEntityBanner;
import mab.common.commander.commands.MBRestartGame;
import mab.common.commander.commands.MBStartGame;
import mab.common.commander.commands.MBStopGame;
import mab.common.commander.items.ItemTrumpet;
import mab.common.commander.npc.EntityMBMeleeUnit;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.common.config.Configuration;

@Mod(name="Mine & Blade: Commander", modid="MaB-Commander", version="0.0.6")
public class MBCommander {
	
	@Instance("MaB-Commander")
	public static MBCommander INSTANCE;
	
	@SidedProxy(clientSide="mab.client.commander.ClientProxy", serverSide="mab.common.commander.CommonProxy")
	public static CommonProxy PROXY;
	
	public static String IMAGE_FOLDER = "/mab/images/";
	public static String ImageSheet = IMAGE_FOLDER+"MBSheet.png";
	
	public static Configuration config;

	public BlockBanner banner;
	public ItemTrumpet trumpet;
	
	@Mod.EventHandler
	public void PreInitialization(FMLPreInitializationEvent event){
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.addCustomCategoryComment(ConfigHelper.CAT_GAMEPLAY, "Global Gameplay Options");
		config.addCustomCategoryComment(ConfigHelper.CAT_UNITS, "Any configuration options to do with units or unit AI");
		
		//ConfigHelper.teamGame = config.get(ConfigHelper.CAT_GAMEPLAY, ConfigHelper.TEAM_GAME, FMLCommonHandler.instance().getSide() != Side.CLIENT).getBoolean(FMLCommonHandler.instance().getSide() != Side.CLIENT);
		//config.get(ConfigHelper.CAT_UNITS, ConfigHelper.UNIT_RANDOM, true);
		//config.get(ConfigHelper.CAT_UNITS, ConfigHelper.UNIT_MELEE_ATTACK_EXP, false);
		//config.get(ConfigHelper.CAT_UNITS, ConfigHelper.UNIT_RANGE_ATTACK_EXP, true);
		
		ConfigHelper.getPathFindSearch();
        banner = new BlockBanner();
        trumpet = new ItemTrumpet();

        GameRegistry.registerBlock(banner, BlockItemBanner.class, "MBBanner");
        GameRegistry.registerTileEntity(TileEntityBanner.class, "MBBanner");
	}

	@Mod.EventHandler
	public void Initialization(FMLInitializationEvent event){
        for(String chan:CommanderPacketHandeler.CHANNELS){
            CommanderPacketHandeler.eventChannel.put(chan, NetworkRegistry.INSTANCE.newEventDrivenChannel(chan));
        }
		NetworkRegistry.INSTANCE.registerGuiHandler(this, PROXY);
		MinecraftForge.EVENT_BUS.register(new MBCommanderForgeEvents());

		EntityRegistry.registerModEntity(EntityMBMeleeUnit.class, "M&B_Unit", 0, this, 50, 3, true);
		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(EntityMBMeleeUnit.class, false);
		
		//Trumpet Recipies
		GameRegistry.addRecipe(new ItemStack(trumpet), "# ", " #", '#', Items.iron_ingot);

		for(int i = 0; i < 16; i++){
			GameRegistry.addRecipe(new ItemStack(banner, 1, i),
				"#", "#", "S", 
				'#', new ItemStack(Blocks.wool, 1, 15-i),
				'S', Items.stick
			);
			
			for(int j = 0; j < 16; j++){
				GameRegistry.addShapelessRecipe(new ItemStack(banner, 1, i),
					new ItemStack(Items.dye, 1, i),
					new ItemStack(banner, 1, j)
				);
			}
			
		}
	}
	
	@Mod.EventHandler
	public void PostInitialization(FMLPostInitializationEvent event){
		PROXY.registerRenderInformation();
		PROXY.registerPlayerTracker();
		config.save();
        //MUD.registerMod(FMLCommonHandler.instance().findContainerFor(this),"update.xml","changelog");
	}
	
	@Mod.EventHandler
	public void ServerStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new MBStartGame());
		event.registerServerCommand(new MBRestartGame());
		event.registerServerCommand(new MBStopGame());
	}
}
