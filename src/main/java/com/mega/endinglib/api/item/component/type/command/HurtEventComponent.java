package com.mega.endinglib.api.item.component.type.command;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public record HurtEventComponent(List<HurtEvent> onDirectEvents, List<HurtEvent> onCausingEvents, List<HurtEvent> onTargetEvents) implements FunctionComponent {
    public static Codec<HurtEventComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.fastUtilListCodec(Codecs.canSerializeAsSingleList(HurtEvent.CODEC)).optionalFieldOf("on_direct_source", List.of()).forGetter(HurtEventComponent::onDirectEvents),
                    Codecs.fastUtilListCodec(Codecs.canSerializeAsSingleList(HurtEvent.CODEC)).optionalFieldOf("on_causing_source", List.of()).forGetter(HurtEventComponent::onCausingEvents),
                    Codecs.fastUtilListCodec(Codecs.canSerializeAsSingleList(HurtEvent.CODEC)).optionalFieldOf("on_target_source", List.of()).forGetter(HurtEventComponent::onTargetEvents)
            ).apply(instance, HurtEventComponent::new)
    );

    public void apply(ServerLevel level, LivingEntity target, LivingEntity causingEntity, DamageSource damageSource) {
        if (this.onDirectEvents.isEmpty() && this.onCausingEvents.isEmpty() && this.onTargetEvents.isEmpty())
            return;
        CommandSourceStack causing_css = causingEntity.createCommandSourceStack();
        CommandSourceStack target_css = target.createCommandSourceStack();
        MinecraftServer server = level.getServer();
        if (!this.onTargetEvents.isEmpty()) {
            this.onTargetEvents.forEach(event -> event.apply(server, target_css));
        }
        if (!this.onCausingEvents.isEmpty()) {
            this.onCausingEvents.forEach(event -> event.apply(server, causing_css));
        }
        Entity direct;
        if (!this.onDirectEvents.isEmpty() && (direct = damageSource.getDirectEntity()) != null) {
            CommandSourceStack direct_css = direct.createCommandSourceStack();
            this.onDirectEvents.forEach(event -> event.apply(server, direct_css));
        }
    }

    public record HurtEvent(String command, Optional<ResourceLocation> function, int minimumPermission) implements FunctionComponent {
        public static Codec<HurtEvent> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("command", "").forGetter(HurtEvent::command),
                        ResourceLocation.CODEC.optionalFieldOf("function").forGetter(HurtEvent::function),
                        Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_permission", 2).forGetter(HurtEvent::minimumPermission)
                ).apply(instance, HurtEvent::new)
        );
        void apply(MinecraftServer server, CommandSourceStack sourceStack) {
            if (!command.isEmpty())
                server.getCommands().performPrefixedCommand(sourceStack.withMaximumPermission(minimumPermission), command);
            function.ifPresent(location -> this.apply(server, sourceStack, location, minimumPermission));
        }
    }
}
