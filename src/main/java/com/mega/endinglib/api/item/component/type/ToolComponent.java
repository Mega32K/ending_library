package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.List;
import java.util.Optional;

public record ToolComponent(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative, List<ToolAction> toolActions) {
    public static final Codec<ToolComponent> CODEC = RecordCodecBuilder.create(
            component -> component.group(
                            Rule.CODEC.listOf().fieldOf("rules").forGetter(ToolComponent::rules),
                            Codec.FLOAT.optionalFieldOf("default_mining_speed", 1.0F).forGetter(ToolComponent::defaultMiningSpeed),
                            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(ToolComponent::damagePerBlock),
                            Codec.BOOL.optionalFieldOf("can_destroy_blocks_in_creative", true).forGetter(ToolComponent::canDestroyBlocksInCreative),
                            Codecs.TOOL_ACTION_CODEC.listOf().optionalFieldOf("tool_actions", List.of()).forGetter(ToolComponent::toolActions)
                    )
                    .apply(component, ToolComponent::new)
    );

    public float getMiningSpeed(BlockState state) {
        for (Rule tool$rule : this.rules) {
            if (tool$rule.speed.isPresent() && state.is(tool$rule.blocks)) {
                return tool$rule.speed.get();
            }
        }

        return this.defaultMiningSpeed;
    }
    public boolean isCorrectForDrops(BlockState state) {
        for (Rule tool$rule : this.rules) {
            if (tool$rule.correctForDrops.isPresent() && state.is(tool$rule.blocks)) {
                return tool$rule.correctForDrops.get();
            }
        }

        return false;
    }
    public record Rule(HolderSet<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(
                p_337954_ -> p_337954_.group(
                                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(Rule::blocks),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed),
                                Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops)
                        )
                        .apply(p_337954_, Rule::new)
        );
    }
}
