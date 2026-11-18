package com.aeropropeller.addon.ponder.generated.scenes;

import com.aeropropeller.addon.ponder.generated.GeneratedPonderSupport;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import java.util.Map;

public final class GeneratedIntegratedPropellerBearing_684135b9 {
    private GeneratedIntegratedPropellerBearing_684135b9() {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation[] tags = new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("simulated", "physics_behavior"), ResourceLocation.fromNamespaceAndPath("simulated", "thrust_blocks")};
        var multi = helper.forComponents(java.util.List.of(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "integrated_propeller_bearing")));
        multi.addStoryBoard(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "generated/ponderer/integrated_propeller_bearing"), GeneratedIntegratedPropellerBearing_684135b9::storyboard$0, tags);
        multi.addStoryBoard(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "generated/ponderer/integrated_propeller_bearing_1"), GeneratedIntegratedPropellerBearing_684135b9::storyboard$1, tags);
        multi.addStoryBoard(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "generated/ponderer/integrated_propeller_bearing_1"), GeneratedIntegratedPropellerBearing_684135b9::storyboard$2, tags);
        multi.addStoryBoard(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "generated/ponderer/integrated_propeller_bearing_1"), GeneratedIntegratedPropellerBearing_684135b9::storyboard$3, tags);
        multi.addStoryBoard(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "generated/ponderer/integrated_propeller_bearing"), GeneratedIntegratedPropellerBearing_684135b9::storyboard$4, tags);
    }

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addTagToComponent(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "integrated_propeller_bearing"), ResourceLocation.fromNamespaceAndPath("simulated", "physics_behavior"));
        helper.addTagToComponent(ResourceLocation.fromNamespaceAndPath("aero_propeller_addon", "integrated_propeller_bearing"), ResourceLocation.fromNamespaceAndPath("simulated", "thrust_blocks"));
    }

    private static void storyboard$0(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("integrated_propeller_bearing_apa_ipb_1", Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.header").getString());
        GeneratedPonderSupport.Context context = new GeneratedPonderSupport.Context();
        GeneratedPonderSupport.showStructure(scene, context, null, null, null, null);
        scene.idle(20);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.text_1").getString(), new Vec3(2.5, 2.0, 2.5), 70, null, true);
        scene.idle(75);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.text_2").getString(), new Vec3(2.5, 2.0, 2.5), 70, null, true);
        scene.idle(75);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.text_3").getString(), new Vec3(2.5, 2.0, 2.5), 70, null, true);
        scene.idle(75);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.text_4").getString(), new Vec3(2.5, 2.0, 2.5), 105, null, true);
        scene.idle(10);
        GeneratedPonderSupport.showControls(scene, new Vec3(2.5, 2.0, 2.5), "down", 20, "right", "aero_propeller_addon:propeller_blade_2", null, false, false);
        scene.idle(35);
        GeneratedPonderSupport.showControls(scene, new Vec3(2.5, 2.0, 2.5), "down", 20, "right", "aero_propeller_addon:propeller_blade_4", null, false, false);
        scene.idle(35);
        GeneratedPonderSupport.showControls(scene, new Vec3(2.5, 2.0, 2.5), "down", 20, "right", "aero_propeller_addon:propeller_blade_6", null, false, false);
        scene.idle(45);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_1.text_5").getString(), new Vec3(2.5, 2.0, 2.5), 100, null, true);
        scene.idle(100);
    }

    private static void storyboard$1(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("integrated_propeller_bearing_apa_ipb_2", Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_2.header").getString());
        GeneratedPonderSupport.Context context = new GeneratedPonderSupport.Context();
        GeneratedPonderSupport.preScanBounds(scene, new BlockPos(1, 2, 2), new BlockPos(3, 3, 3));
        GeneratedPonderSupport.showStructure(scene, context, null, null, null, 180.0f);
        scene.idle(20);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_2.text_1").getString(), new Vec3(3.5, 3.0, 2.5), 70, null, true);
        scene.idle(75);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_2.text_2").getString(), new Vec3(3.5, 4.0, 2.5), 70, null, true);
        scene.idle(70);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 20, "right", null, null, false, false);
        scene.idle(20);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:2,BladeLevel:2,ConnectedToExtraKinetics:0b,DisassemblySlowdown:0b,LastGenerated:-64.0f,Network:{AddedStress:24.0f,Capacity:1048576.0f,Id:-31885837270971L,Size:8,Stress:1536.0f},Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:72.677216f,RotorAngularSpeed:-19.199999f,Running:0b,ScrollValue:1,Source:[I;-115,71,-14],Speed:-64.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        scene.idle(20);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_2.text_3").getString(), new Vec3(3.5, 5.0, 2.5), 100, null, true);
        scene.idle(100);
        scene.addKeyframe();
        GeneratedPonderSupport.replaceBlocks(scene, context, "create:gearshift", null, new BlockPos(1, 2, 2), new BlockPos(1, 2, 2), null);
        GeneratedPonderSupport.setBlock(scene, context, "minecraft:lever", Map.ofEntries(Map.entry("face", "wall"), Map.entry("powered", "false"), Map.entry("facing", "south")), new BlockPos(1, 2, 3), new BlockPos(1, 2, 3), null, false, false, "down", 20, 1, false, null, "down");
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("powered", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:5,Stress:0.0f},Source:[I;-118,71,-14],Speed:-64.0f}", null, new BlockPos(1, 2, 2), new BlockPos(1, 2, 2));
        scene.idle(20);
        GeneratedPonderSupport.showControls(scene, new Vec3(1.5, 3.0, 3.5), "down", 20, "right", null, null, false, false);
        scene.idle(30);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("face", "wall"), Map.entry("powered", "true"), Map.entry("facing", "south")), null, null, new BlockPos(1, 2, 3), new BlockPos(1, 2, 3));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("powered", "true"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:5,Stress:0.0f},Source:[I;-118,71,-14],Speed:-64.0f}", null, new BlockPos(1, 2, 2), new BlockPos(1, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("axis", "z")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:8,Stress:1536.0f},Source:[I;-116,71,-14],Speed:64.0f}", null, new BlockPos(3, 2, 2), new BlockPos(3, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:2,BladeLevel:2,ConnectedToExtraKinetics:0b,DisassemblySlowdown:0b,LastGenerated:64.0f,Network:{AddedStress:24.0f,Capacity:1048576.0f,Id:-31885837270971L,Size:8,Stress:1536.0f},Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:311.31274f,RotorAngularSpeed:19.199999f,Running:0b,ScrollValue:1,Source:[I;-115,71,-14],Speed:64.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb_2.text_4").getString(), new Vec3(3.5, 4.0, 2.5), 70, null, true);
    }

    private static void storyboard$2(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("integrated_propeller_bearing_apa_ipb3", Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.header").getString());
        GeneratedPonderSupport.Context context = new GeneratedPonderSupport.Context();
        GeneratedPonderSupport.preScanBounds(scene, new BlockPos(2, 2, 2), new BlockPos(3, 3, 3));
        GeneratedPonderSupport.showStructure(scene, context, null, null, null, 180.0f);
        scene.idle(20);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_1").getString(), new Vec3(4.5, 4.0, 3.5), 70, null, true);
        scene.idle(75);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_2").getString(), new Vec3(3.5, 4.0, 2.5), 70, "red", true);
        scene.idle(75);
        GeneratedPonderSupport.showControls(scene, new Vec3(2.5, 3.0, 3.5), "down", 20, "right", null, null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_3").getString(), new Vec3(3.5, 4.0, 2.5), 30, "red", true);
        scene.idle(40);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("face", "wall"), Map.entry("powered", "true"), Map.entry("facing", "south")), null, null, new BlockPos(2, 2, 3), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("powered", "true"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:6,Stress:0.0f},Source:[I;-117,71,-14],Speed:-64.0f}", null, new BlockPos(2, 2, 3), new BlockPos(2, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("axis", "z")), "{Speed:0.0f}", null, new BlockPos(3, 2, 2), new BlockPos(3, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:2,BladeLevel:2,DisassemblySlowdown:0b,LastGenerated:-64.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:91.194786f,RotorAngularSpeed:-1.4E-45f,Running:0b,ScrollValue:1,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_4").getString(), new Vec3(3.5, 4.0, 2.5), 30, "green", true);
        scene.idle(40);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_5").getString(), new Vec3(3.5, 4.0, 2.5), 70, null, true);
        scene.idle(75);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_6").getString(), new Vec3(3.5, 4.0, 2.5), 40, "blue", true);
        scene.idle(45);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_7").getString(), new Vec3(3.5, 4.0, 2.5), 40, null, true);
        scene.idle(40);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 20, "right", "aero_propeller_addon:propeller_blade_2", null, false, false);
        scene.idle(30);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:3,BladeLevel:2,DisassemblySlowdown:0b,LastGenerated:-64.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:91.194786f,RotorAngularSpeed:-1.4E-45f,Running:0b,ScrollValue:1,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_8").getString(), new Vec3(3.5, 4.0, 2.5), 30, "blue", true);
        scene.idle(35);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 20, "right", "aero_propeller_addon:propeller_blade_2", null, false, false);
        scene.idle(20);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:4,BladeLevel:2,DisassemblySlowdown:0b,LastGenerated:-64.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:91.194786f,RotorAngularSpeed:-1.4E-45f,Running:0b,ScrollValue:1,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_9").getString(), new Vec3(3.5, 4.0, 2.5), 30, "blue", true);
        scene.idle(35);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_10").getString(), new Vec3(3.5, 4.0, 2.5), 70, "red", true);
        scene.idle(75);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 30, "right", "aero_propeller_addon:propeller_blade_4", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_11").getString(), new Vec3(3.5, 4.0, 2.5), 40, "blue", true);
        scene.idle(50);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 30, null, "minecraft:barrier", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_12").getString(), new Vec3(3.5, 4.0, 2.5), 40, "red", true);
        scene.idle(50);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_13").getString(), new Vec3(3.5, 4.0, 2.5), 50, "white", true);
        scene.idle(40);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 20, "right", null, null, false, false);
        scene.idle(25);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:3,BladeLevel:2,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        scene.idle(30);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_14").getString(), new Vec3(3.5, 4.0, 2.5), 50, "blue", true);
        scene.idle(60);
        scene.addKeyframe();
        GeneratedPonderSupport.destroyBlock(scene, context, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2), null);
        GeneratedPonderSupport.createItemEntity(scene, context, "aero_propeller_addon:propeller_blade_2", 3, new Vec3(3.5, 3.0, 2.5), new Vec3(0.0, 0.0, 0.0), null, null);
        scene.idle(20);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_15").getString(), new Vec3(3.5, 3.0, 2.5), 60, null, true);
        scene.idle(40);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 3.5, 2.5), "down", 20, null, "aero_propeller_addon:propeller_blade_2", null, false, false);
        scene.idle(40);
        GeneratedPonderSupport.clearItemEntities(scene, context, true, "aero_propeller_addon:propeller_blade_2", null, null, null);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "up")), new BlockPos(3, 3, 2), new BlockPos(3, 3, 2), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:-64.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:62.41316f,RotorAngularSpeed:-1.9567629E-26f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.idle(30);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_16").getString(), new Vec3(3.5, 4.0, 2.5), 60, "blue", true);
        scene.idle(65);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 30, "right", "aero_propeller_addon:propeller_blade_4", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_17").getString(), new Vec3(3.5, 4.0, 2.5), 40, "blue", true);
        scene.idle(45);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:1,BladeLevel:4,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        scene.idle(30);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_18").getString(), new Vec3(3.5, 4.0, 2.5), 60, "red", true);
        scene.idle(50);
        GeneratedPonderSupport.showControls(scene, new Vec3(2.5, 3.0, 3.5), "down", 20, "right", null, null, false, false);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("powered", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:6,Stress:0.0f},Source:[I;-117,71,-14],Speed:-64.0f}", null, new BlockPos(2, 2, 2), new BlockPos(2, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("axis", "z")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:7,Stress:0.0f},Source:[I;-116,71,-14],Speed:-64.0f}", null, new BlockPos(3, 2, 2), new BlockPos(3, 2, 2));
        scene.idle(20);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 40, null, "minecraft:barrier", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_19").getString(), new Vec3(3.5, 4.0, 2.5), 40, "red", true);
        scene.idle(50);
        scene.addKeyframe();
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_20").getString(), new Vec3(3.5, 4.0, 2.5), 50, null, true);
        scene.idle(55);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_21").getString(), new Vec3(3.5, 4.0, 2.5), 50, null, true);
        scene.idle(60);
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 30, "right", "aero_propeller_addon:propeller_blade_4", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_22").getString(), new Vec3(3.5, 4.0, 2.5), 40, "blue", true);
        scene.idle(45);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:2,BladeLevel:4,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb3.text_23").getString(), new Vec3(3.5, 4.0, 2.5), 30, "blue", true);
        scene.idle(30);
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:2,BladeLevel:4,ConnectedToExtraKinetics:0b,DisassemblySlowdown:0b,LastGenerated:-64.0f,Network:{AddedStress:36.0f,Capacity:1048576.0f,Id:-31885837270971L,Size:8,Stress:2304.0f},Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.68219185f,RotorAngularSpeed:-19.199968f,Running:0b,ScrollValue:0,Source:[I;-115,71,-14],Speed:-64.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
    }

    private static void storyboard$3(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("integrated_propeller_bearing_apa_ipb4", Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.header").getString());
        GeneratedPonderSupport.Context context = new GeneratedPonderSupport.Context();
        GeneratedPonderSupport.preScanBounds(scene, new BlockPos(0, 0, 0), new BlockPos(5, 3, 2));
        GeneratedPonderSupport.showStructure(scene, context, null, null, null, 180.0f);
        scene.idle(30);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_1").getString(), new Vec3(3.5, 4.0, 2.5), 60, null, true);
        scene.idle(70);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_2").getString(), new Vec3(3.5, 4.0, 2.5), 60, "blue", true);
        scene.idle(70);
        scene.addKeyframe();
        GeneratedPonderSupport.setBlock(scene, context, "create:stressometer", null, new BlockPos(4, 2, 2), new BlockPos(4, 2, 2), null, false, false, "down", 20, 1, false, null, "down");
        GeneratedPonderSupport.setBlock(scene, context, "create:speedometer", null, new BlockPos(5, 2, 2), new BlockPos(5, 2, 2), null, false, false, "down", 20, 1, false, null, "down");
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up"), Map.entry("axis_along_first", "true")), "{Color:65280,ConnectedToExtraKinetics:0b,HasAttachedComputer:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:9,Stress:1536.0f},Source:[I;-115,71,-14],Speed:64.0f,Value:0.0014648438f}", null, new BlockPos(4, 2, 2), new BlockPos(4, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up"), Map.entry("axis_along_first", "true")), "{Color:4170495,ConnectedToExtraKinetics:0b,HasAttachedComputer:0b,Network:{Capacity:1048576.0f,Id:-31885837270971L,Size:10,Stress:1536.0f},Source:[I;-114,71,-14],Speed:64.0f,Value:0.5957143f}", null, new BlockPos(5, 2, 2), new BlockPos(5, 2, 2));
        scene.idle(30);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_3").getString(), new Vec3(5.5, 3.0, 2.5), 50, "medium", true);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_4").getString(), new Vec3(3.5, 4.0, 2.5), 50, "blue", true);
        scene.idle(60);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_5").getString(), new Vec3(3.5, 4.0, 2.5), 60, "white", true);
        scene.idle(70);
        GeneratedPonderSupport.showControls(scene, new Vec3(4.5, 3.0, 2.5), "down", 45, null, "create:goggles", null, false, false);
        scene.idle(10);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_6").getString(), new Vec3(4.5, 3.0, 2.5), 30, "green", true);
        scene.idle(60);
        scene.addKeyframe();
        GeneratedPonderSupport.showControls(scene, new Vec3(3.5, 4.0, 2.5), "down", 60, null, "create:goggles", null, false, false);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_7").getString(), new Vec3(3.5, 4.0, 2.5), 60, "blue", true);
        scene.idle(70);
        scene.addKeyframe();
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "west")), "{HasAttachedComputer:0b,Network:{AddedCapacity:16384.0f,Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},ScrollValue:256,Speed:-256.0f}", null, new BlockPos(1, 0, 0), new BlockPos(1, 0, 0));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("waterlogged", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Material:{Name:\"minecraft:spruce_planks\"},Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-117,69,-16],Speed:-256.0f}", null, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("waterlogged", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Material:{Name:\"minecraft:spruce_planks\"},Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-118,69,-16],Speed:128.0f}", null, new BlockPos(0, 1, 1), new BlockPos(0, 1, 1));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("waterlogged", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Material:{Name:\"minecraft:spruce_planks\"},Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-118,70,-15],Speed:-256.0f}", null, new BlockPos(0, 2, 2), new BlockPos(0, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("waterlogged", "false"), Map.entry("axis", "x")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-118,71,-14],Speed:-256.0f}", null, new BlockPos(1, 2, 2), new BlockPos(1, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("axis", "z")), "{ConnectedToExtraKinetics:0b,Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-116,71,-14],Speed:-256.0f}", null, new BlockPos(3, 2, 2), new BlockPos(3, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up"), Map.entry("axis_along_first", "true")), "{Color:196352,ConnectedToExtraKinetics:0b,HasAttachedComputer:0b,Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-115,71,-14],Speed:256.0f,Value:0.004760742f}", null, new BlockPos(4, 2, 2), new BlockPos(4, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up"), Map.entry("axis_along_first", "true")), "{Color:16744447,ConnectedToExtraKinetics:0b,HasAttachedComputer:0b,Network:{Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Source:[I;-114,71,-14],Speed:256.0f,Value:1.125f}", null, new BlockPos(5, 2, 2), new BlockPos(5, 2, 2));
        GeneratedPonderSupport.modifyBlockEntity(scene, Map.ofEntries(Map.entry("facing", "up")), "{Angle:0.0f,BladeCount:5,BladeLevel:8,ConnectedToExtraKinetics:0b,DisassemblySlowdown:0b,LastGenerated:-256.0f,Network:{AddedStress:78.0f,Capacity:4194304.0f,Id:-31885837270971L,Size:10,Stress:19968.0f},Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:328.5226f,RotorAngularSpeed:-76.79999f,Running:0b,ScrollValue:0,Source:[I;-115,71,-14],Speed:-256.0f}", null, new BlockPos(3, 3, 2), new BlockPos(3, 3, 2));
        scene.idle(50);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_8").getString(), new Vec3(3.5, 4.0, 2.5), 60, "blue", true);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_9").getString(), new Vec3(5.5, 3.0, 2.5), 60, "fast", true);
        scene.idle(70);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb4.text_10").getString(), new Vec3(3.5, 4.0, 2.5), 60, "blue", true);
        scene.idle(100);
    }

    private static void storyboard$4(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("integrated_propeller_bearing_apa_ipb5", Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb5.header").getString());
        GeneratedPonderSupport.Context context = new GeneratedPonderSupport.Context();
        GeneratedPonderSupport.preScanBounds(scene, new BlockPos(1, 1, 1), new BlockPos(3, 1, 3));
        GeneratedPonderSupport.showStructure(scene, context, null, null, null, null);
        scene.idle(20);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "east")), new BlockPos(2, 1, 3), new BlockPos(2, 1, 3), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.idle(20);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "south")), new BlockPos(1, 1, 2), new BlockPos(1, 1, 2), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.idle(20);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "west")), new BlockPos(2, 1, 1), new BlockPos(2, 1, 1), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.idle(20);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "north")), new BlockPos(3, 1, 2), new BlockPos(3, 1, 2), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.idle(20);
        GeneratedPonderSupport.setBlock(scene, context, "aero_propeller_addon:integrated_propeller_bearing", Map.ofEntries(Map.entry("facing", "down")), new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), "{Angle:0.0f,BladeCount:0,BladeLevel:0,DisassemblySlowdown:0b,LastGenerated:0.0f,Owner:[I;-671478984,-1717745231,-1475005766,-1212476830],RotationSpeed:0.0f,RotorAngle:0.0f,RotorAngularSpeed:0.0f,Running:0b,ScrollValue:0,Speed:0.0f}", false, false, "down", 20, 1, false, null, "down");
        scene.addKeyframe();
        scene.idle(40);
        GeneratedPonderSupport.showText(scene, Component.translatable("aero_propeller_addon.ponder.integrated_propeller_bearing_apa_ipb5.text_1").getString(), new Vec3(2.5, 2.0, 2.5), 60, "blue", true);
        scene.idle(80);
    }

}
