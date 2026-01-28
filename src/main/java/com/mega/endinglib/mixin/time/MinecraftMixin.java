package com.mega.endinglib.mixin.time;

import com.mega.endinglib.api.event.client.TimeStoppedClientTickEvent;
import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.mixin.accessor.AccessorClientLevel;
import com.mega.endinglib.mixin.accessor.AccessorMcTimer;
import com.mega.endinglib.util.mixin.bettercombat.BetterCombatTicker;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Timer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Final
    @Shadow
    public Gui gui;
    @Final
    @Shadow
    public GameRenderer gameRenderer;
    @Shadow
    @Nullable
    public Screen screen;
    @Shadow
    @Nullable
    public HitResult hitResult;
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;
    @Final
    @Shadow
    public Options options;
    @Final
    @Shadow
    public KeyboardHandler keyboardHandler;
    @Shadow
    protected int missTime;
    @Unique
    protected boolean uom$isTimeStop = false;
    @Shadow
    private ProfilerFiller profiler;
    @Shadow
    private int rightClickDelay;
    @Shadow
    @Nullable
    private Overlay overlay;
    @Final
    @Shadow
    private Timer timer;
    @Shadow(remap = false)
    private float realPartialTick;
    @Shadow
    private float pausePartialTick;
    @Final
    @Shadow
    private ChatListener chatListener;
    @Final
    @Shadow
    private Tutorial tutorial;
    @Shadow
    @Nullable
    private IntegratedServer singleplayerServer;
    @Shadow
    private volatile boolean pause;
    @Final
    @Shadow
    private SoundManager soundManager;

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract void setScreen(@org.jetbrains.annotations.Nullable Screen p_91153_);

    @Shadow
    protected abstract void handleKeybinds();

    @Shadow
    public abstract boolean hasSingleplayerServer();

    @Inject(method = "getPartialTick", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void getPartialTick(CallbackInfoReturnable<Float> cir) {
        if (TimeStopUtils.isTimeStop && ClientContext.isTimeStop_andSameDimension)
            cir.setReturnValue(timer.partialTick);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickStart(F)V", remap = false))
    private void runTick_modifyPartial(boolean p_91384_, CallbackInfo ci) {
        if (!p_91384_) return;
        long l = TimeContext.Client.timer.advanceTime(Util.getMillis());
        if (level == null && TimeStopUtils.isTimeStop) {
            ClientContext.isTimeStop_andSameDimension = false;
            TimeStopUtils.isTimeStop = false;
        }
        AccessorMcTimer accessorMcTimer = (AccessorMcTimer) this.timer;
        uom$isTimeStop = TimeStopUtils.isTimeStop && ClientContext.isTimeStop_andSameDimension;
        if (TimeStopUtils.isTimeStop && gameMode != null && player != null) {
            if (uom$isTimeStop && !pause) {
                accessorMcTimer.setMsPerTick(1.0e32F);
                realPartialTick = timer.partialTick;
                for (int i = 0; i < l; i++) {
                    System.out.println(l);
                    MinecraftForge.EVENT_BUS.post(new TimeStoppedClientTickEvent(TickEvent.Phase.START));
                    this.profiler.push("BetterCombatHead");
                    if ((Object) this instanceof BetterCombatTicker ticker) {
                        ticker.tickHead();
                    }
                    this.profiler.pop();
                    if (this.overlay == null && this.screen == null) {
                        this.profiler.push("Keybindings");
                        this.handleKeybinds();
                        if (this.missTime > 0) {
                            --this.missTime;
                        }


                    }
                    this.gameRenderer.tick();
                    this.profiler.push("keyboard");
                    this.keyboardHandler.tick();
                    this.profiler.pop();
                    if (level != null) {
                        if (this.screen == null && this.player != null) {
                            if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
                                this.setScreen(null);
                            } else if (this.player.isSleeping()) {
                                this.setScreen(new InBedChatScreen());
                            }
                        } else {
                            Screen $$4 = this.screen;
                            if ($$4 instanceof InBedChatScreen inbedchatscreen) {
                                if (this.player != null && !this.player.isSleeping()) {
                                    inbedchatscreen.onPlayerWokeUp();
                                }
                            }
                        }

                        if (this.screen != null) {
                            this.missTime = 10000;
                        }

                        if (this.screen != null) {
                            Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
                        }
                        if (player != null && (player.isSpectator() || player.isCreative() || TimeStopUtils.canMove(player))) {
                            if (this.rightClickDelay > 0) {
                                --this.rightClickDelay;
                            }

                            this.profiler.push("gui");
                            this.chatListener.tick();
                            this.gui.tick(false);
                            this.profiler.pop();
                            this.gameRenderer.pick(1.0F);
                            this.tutorial.onLookAt(this.level, this.hitResult);
                            this.profiler.push("gameMode");
                            if (this.gameMode != null) {
                                this.gameMode.tick();
                            }
                            this.profiler.pop();
                            if (!this.options.renderDebug) {
                                this.gui.clearCache();
                            }
                        }
                        if (level != null) {
                            ((AccessorClientLevel) level).getTickingEntities().forEach((entity) -> {
                                if (!entity.isRemoved() && !entity.isPassenger()) {
                                    if (TimeStopUtils.canMove(entity)) {
                                        level.guardEntityTick(level::tickNonPassenger, entity);
                                    }
                                }
                            });

                            try {
                                this.level.tick(() -> true);
                            } catch (Throwable throwable) {
                                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception in world tick");
                                if (this.level == null) {
                                    CrashReportCategory crashreportcategory = crashreport.addCategory("Affected level");
                                    crashreportcategory.setDetail("Problem", "Level is null!");
                                } else {
                                    this.level.fillReportDetails(crashreport);
                                }

                                throw new ReportedException(crashreport);
                            }
                        }
                    }
                    this.profiler.push("BetterCombatTail");
                    if ((Object) this instanceof BetterCombatTicker ticker) {
                        ticker.tickTail();
                    }
                    this.profiler.pop();

                    MinecraftForge.EVENT_BUS.post(new TimeStoppedClientTickEvent(TickEvent.Phase.END));
                }

            }
            if (uom$isTimeStop) {
                if (screen instanceof DeathScreen) screen.tick();
                for (int i = 0; i < l; i++) {
                    this.soundManager.tick(true);
                }
            }
        } else {
            if (Math.abs(accessorMcTimer.getMsPerTick() - 1.0e32F) < 0.001F) accessorMcTimer.setMsPerTick(50.0F);
        }
    }


}
