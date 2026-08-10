package net.minecraft.game.item.potion;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BrewingSystem {
    private static final Int2ObjectMap<String> potionEffects = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<String> potionAmplifiers = new Int2ObjectOpenHashMap<>();
    private static final String[] namePrefixes = new String[]{
        "potion.prefix.mundane",
        "potion.prefix.uninteresting",
        "potion.prefix.bland",
        "potion.prefix.clear",
        "potion.prefix.milky",
        "potion.prefix.diffuse",
        "potion.prefix.artless",
        "potion.prefix.thin",
        "potion.prefix.awkward",
        "potion.prefix.flat",
        "potion.prefix.bulky",
        "potion.prefix.bungling",
        "potion.prefix.buttered",
        "potion.prefix.smooth",
        "potion.prefix.suave",
        "potion.prefix.debonair",
        "potion.prefix.thick",
        "potion.prefix.elegant",
        "potion.prefix.fancy",
        "potion.prefix.charming",
        "potion.prefix.dashing",
        "potion.prefix.refined",
        "potion.prefix.cordial",
        "potion.prefix.sparkling",
        "potion.prefix.potent",
        "potion.prefix.foul",
        "potion.prefix.odorless",
        "potion.prefix.rank",
        "potion.prefix.harsh",
        "potion.prefix.acrid",
        "potion.prefix.gross",
        "potion.prefix.stinky"
    };

    public static boolean checkFlag(int potionValue, int bitIndex) {
        return (potionValue & 1 << bitIndex) != 0;
    }

    private static int getDur(int potionValue, int bitIndex) {
        return checkFlag(potionValue, bitIndex) ? 1 : 0;
    }

    private static int getAmp(int potionValue, int bitIndex) {
        return checkFlag(potionValue, bitIndex) ? 0 : 1;
    }

    public static int getEffect(int potionValue) {
        return finalizeEffect(potionValue, 14, 9, 7, 3, 2);
    }

    public static int getColor(int potionValue) {
        int red = (finalizeEffect(potionValue, 2, 14, 11, 8, 5) ^ 3) << 3;
        int green = (finalizeEffect(potionValue, 0, 12, 9, 6, 3) ^ 6) << 3;
        int blue = (finalizeEffect(potionValue, 13, 10, 4, 1, 7) ^ 8) << 3;
        return red << 16 | green << 8 | blue;
    }

    public static String potionNames(int potionValue) {
        return namePrefixes[getEffect(potionValue)];
    }

    private static int metaCheck(boolean check1, boolean check2, boolean check3, int type, int bitIndex, int multiplier, int potionBits) {
        int meta = 0;
        if (check1) {
            meta = getAmp(potionBits, bitIndex);
        } else if (type != -1) {
            if (type == 0 && findValidEffect(potionBits) == bitIndex) {
                meta = 1;
            } else if (type == 1 && findValidEffect(potionBits) > bitIndex) {
                meta = 1;
            } else if (type == 2 && findValidEffect(potionBits) < bitIndex) {
                meta = 1;
            }
        } else {
            meta = getDur(potionBits, bitIndex);
        }

        if (check2) {
            meta *= multiplier;
        }

        if (check3) {
            meta *= -1;
        }

        return meta;
    }

    private static int findValidEffect(int val) {
        int i;
        for (i = 0; val > 0; i++) {
            val &= val - 1;
        }

        return i;
    }

    private static int effectIterator(String expression, int start, int end, int bits) {
        if (start < expression.length() && end >= 0 && start < end) {
            int or = expression.indexOf(124, start);
            if (or >= 0 && or < end) {
                int result = effectIterator(expression, start, or - 1, bits);
                if (result > 0) {
                    return result;
                }

                int right = effectIterator(expression, or + 1, end, bits);
                return Math.max(right, 0);
            } else {
                int result = expression.indexOf(38, start);
                if (result >= 0 && result < end) {
                    int right = effectIterator(expression, start, result - 1, bits);
                    if (right <= 0) {
                        return 0;
                    }

                    int i = effectIterator(expression, result + 1, end, bits);
                    return i <= 0 ? 0 : Math.max(right, i);
                } else {
                    boolean readMultiplier = false;
                    boolean hasMultiplier = false;
                    boolean readNumber = false;
                    boolean invert = false;
                    boolean negative = false;
                    byte type = -1;
                    int bit = 0;
                    int multiplier = 0;
                    int total = 0;

                    for (int cursor = start; cursor < end; cursor++) {
                        char character = expression.charAt(cursor);
                        if (character >= '0' && character <= '9') {
                            if (readMultiplier) {
                                multiplier = character - '0';
                                hasMultiplier = true;
                            } else {
                                bit *= 10;
                                bit += character - '0';
                                readNumber = true;
                            }
                        } else if (character == '*') {
                            readMultiplier = true;
                        } else if (character == '!') {
                            if (readNumber) {
                                total += metaCheck(invert, hasMultiplier, negative, type, bit, multiplier, bits);
                                negative = false;
                                readMultiplier = false;
                                hasMultiplier = false;
                                readNumber = false;
                                multiplier = 0;
                                bit = 0;
                                type = -1;
                            }

                            invert = true;
                        } else if (character == '-') {
                            if (readNumber) {
                                total += metaCheck(invert, hasMultiplier, negative, type, bit, multiplier, bits);
                                invert = false;
                                readMultiplier = false;
                                hasMultiplier = false;
                                readNumber = false;
                                multiplier = 0;
                                bit = 0;
                                type = -1;
                            }

                            negative = true;
                        } else if (character != '=' && character != '<' && character != '>') {
                            if (character == '+' && readNumber) {
                                total += metaCheck(invert, hasMultiplier, negative, type, bit, multiplier, bits);
                                invert = false;
                                negative = false;
                                readMultiplier = false;
                                hasMultiplier = false;
                                readNumber = false;
                                multiplier = 0;
                                bit = 0;
                                type = -1;
                            }
                        } else {
                            if (readNumber) {
                                total += metaCheck(invert, hasMultiplier, negative, type, bit, multiplier, bits);
                                invert = false;
                                negative = false;
                                readMultiplier = false;
                                hasMultiplier = false;
                                readNumber = false;
                                multiplier = 0;
                                bit = 0;
                                type = -1;
                            }

                            if (character == '=') {
                                type = 0;
                            } else if (character == '<') {
                                type = 2;
                            } else if (character == '>') {
                                type = 1;
                            }
                        }
                    }

                    if (readNumber) {
                        total += metaCheck(invert, hasMultiplier, negative, type, bit, multiplier, bits);
                    }

                    return total;
                }
            }
        } else {
            return 0;
        }
    }

    public static List<EffectData> getPotionEffect(int bits) {
        ArrayList<EffectData> effects = null;

        for (Effect effect : EffectList.effects) {
            if (effect != null) {
                String expression = potionEffects.get(effect.getId());
                if (expression != null) {
                    int duration = effectIterator(expression, 0, expression.length(), bits);
                    if (duration > 0) {
                        int amplifier = 0;
                        String ampStr = potionAmplifiers.get(effect.getId());
                        if (ampStr != null) {
                            amplifier = effectIterator(ampStr, 0, ampStr.length(), bits);
                            if (amplifier < 0) {
                                amplifier = 0;
                            }
                        }

                        if (effect.isInstant()) {
                            duration = 1;
                        } else {
                            duration = 1200 * (duration * 3 + (duration - 1) * 2);
                            if (effect.isNegative()) {
                                duration >>= 1;
                            }

                            duration >>= amplifier;
                            duration = (int)Math.round(duration * effect.getEffectiveness());
                            if ((bits & 16384) != 0) {
                                duration = (int)Math.round(duration * 0.75 + 0.5);
                            }
                        }

                        if (effects == null) {
                            effects = new ArrayList<>();
                        }

                        effects.add(new EffectData(effect.getId(), duration, amplifier));
                    }
                }
            }
        }

        return effects;
    }

    private static int brewBitOperations(int potionValue, int bitIndex, boolean resetBit, boolean flipBit, boolean checkBit) {
        if (checkBit) {
            if (!checkFlag(potionValue, bitIndex)) {
                return 0;
            }
        } else if (resetBit) {
            potionValue &= ~(1 << bitIndex);
        } else if (flipBit) {
            if ((potionValue & 1 << bitIndex) != 0) {
                potionValue &= ~(1 << bitIndex);
            } else {
                potionValue |= 1 << bitIndex;
            }
        } else {
            potionValue |= 1 << bitIndex;
        }

        return potionValue;
    }

    public static int applyIngredient(int potionValue, String ingredientActions) {
        byte b2 = 0;
        int actionsLength = ingredientActions.length();
        boolean haveBitIndex = false;
        boolean flipBit = false;
        boolean resetBit = false;
        boolean checkBit = false;
        int bitIndex = 0;

        for (int i9 = b2; i9 < actionsLength; i9++) {
            char curChar = ingredientActions.charAt(i9);
            if (curChar >= '0' && curChar <= '9') {
                bitIndex *= 10;
                bitIndex += curChar - '0';
                haveBitIndex = true;
            } else if (curChar == '!') {
                if (haveBitIndex) {
                    potionValue = brewBitOperations(potionValue, bitIndex, resetBit, flipBit, checkBit);
                    checkBit = false;
                    resetBit = false;
                    haveBitIndex = false;
                    bitIndex = 0;
                }

                flipBit = true;
            } else if (curChar == '-') {
                if (haveBitIndex) {
                    potionValue = brewBitOperations(potionValue, bitIndex, resetBit, flipBit, checkBit);
                    checkBit = false;
                    flipBit = false;
                    haveBitIndex = false;
                    bitIndex = 0;
                }

                resetBit = true;
            } else if (curChar == '+') {
                if (haveBitIndex) {
                    potionValue = brewBitOperations(potionValue, bitIndex, resetBit, flipBit, checkBit);
                    checkBit = false;
                    flipBit = false;
                    resetBit = false;
                    haveBitIndex = false;
                    bitIndex = 0;
                }
            } else if (curChar == '&') {
                if (haveBitIndex) {
                    potionValue = brewBitOperations(potionValue, bitIndex, resetBit, flipBit, checkBit);
                    flipBit = false;
                    resetBit = false;
                    haveBitIndex = false;
                    bitIndex = 0;
                }

                checkBit = true;
            }
        }

        if (haveBitIndex) {
            potionValue = brewBitOperations(potionValue, bitIndex, resetBit, flipBit, checkBit);
        }

        return potionValue & 32767;
    }

    public static int finalizeEffect(int i0, int i1, int i2, int i3, int i4, int i5) {
        return (checkFlag(i0, i1) ? 16 : 0)
            | (checkFlag(i0, i2) ? 8 : 0)
            | (checkFlag(i0, i3) ? 4 : 0)
            | (checkFlag(i0, i4) ? 2 : 0)
            | (checkFlag(i0, i5) ? 1 : 0);
    }

    public static int updatePotions(Collection<EffectData> cock) {
        int rgb = 3694022;
        if (cock != null && !cock.isEmpty()) {
            float r = 0.0F;
            float g = 0.0F;
            float b = 0.0F;
            float a = 0.0F;

            for (EffectData pe : cock) {
                int colol = EffectList.effects[pe.getPotionID()].getLiquidColor();

                for (int i = 0; i <= pe.getPotionAmplifier(); i++) {
                    r += (colol >> 16 & 0xFF) / 255.0F;
                    g += (colol >> 8 & 0xFF) / 255.0F;
                    b += (colol & 0xFF) / 255.0F;
                    a++;
                }
            }

            r = r / a * 255.0F;
            g = g / a * 255.0F;
            b = b / a * 255.0F;
            return (int)r << 16 | (int)g << 8 | (int)b;
        } else {
            return rgb;
        }
    }

    static {
        potionEffects.put(EffectList.swiftness.getId(), "!10 & !4 & 5 & 0 & >2 | !7 & !4 & 5 & 0 & >2");
        potionEffects.put(EffectList.slowness.getId(), "10 & 7 & !4 & 7+5+1-0");
        potionEffects.put(EffectList.haste.getId(), "2 & 12+2+6-1-7 & <8");
        potionEffects.put(EffectList.miningFatigue.getId(), "!2 & !1*2-9 & 14-5");
        potionEffects.put(EffectList.strength.getId(), "9 & 3 & 9+4+5 & <11");
        potionEffects.put(EffectList.heal.getId(), "11 & <6");
        potionEffects.put(EffectList.harm.getId(), "!11 & 1 & 10 & !7");
        potionEffects.put(EffectList.jump.getId(), "8 & 2+0 & <5");
        potionEffects.put(EffectList.confusion.getId(), "8*2-!7+4-11 & !2 | 13 & 11 & 2*3-1-5");
        potionEffects.put(EffectList.regeneration.getId(), "!14 & 13*3-!0-!5-8");
        potionEffects.put(EffectList.resistance.getId(), "10 & 4 & 10+5+6 & <9");
        potionEffects.put(EffectList.fireResistance.getId(), "14 & !5 & 6-!1 & 14+13+12");
        potionEffects.put(EffectList.waterBreathing.getId(), "0+1+12 & !6 & 10 & !11 & !13");
        potionEffects.put(EffectList.invisibility.getId(), "2+5+13-0-4 & !7 & !1 & >5");
        potionEffects.put(EffectList.blindness.getId(), "9 & !1 & !5 & !3 & =3");
        potionEffects.put(EffectList.nightVision.getId(), "8*2-!7 & 5 & !0 & >3");
        potionEffects.put(EffectList.nausea.getId(), ">4>6>8-3-8+2");
        potionEffects.put(EffectList.weakness.getId(), "=1>5>7>9+3-7-2-11 & !10 & !0");
        potionEffects.put(EffectList.poison.getId(), "12+9 & !13 & !0");
        potionEffects.put(EffectList.fatigue.getId(), "12 & !13 & >3 +5 -7");
        potionAmplifiers.put(EffectList.swiftness.getId(), "7+!3-!1");
        potionAmplifiers.put(EffectList.haste.getId(), "1+0-!11");
        potionAmplifiers.put(EffectList.strength.getId(), "2+7-!12");
        potionAmplifiers.put(EffectList.heal.getId(), "11+!0-!1-!14");
        potionAmplifiers.put(EffectList.harm.getId(), "!11-!14+!0-!1");
        potionAmplifiers.put(EffectList.resistance.getId(), "12-!2");
        potionAmplifiers.put(EffectList.poison.getId(), "14>5");
    }
}
