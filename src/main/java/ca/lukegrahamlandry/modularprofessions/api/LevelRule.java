package ca.lukegrahamlandry.modularprofessions.api;

import com.google.gson.JsonObject;

public interface LevelRule {
    static LevelRule parse(JsonObject leveling) {
        String type = leveling.get("type").getAsString();
        if (type.equals("linear")){
            return new Linear(leveling.get("scale").getAsFloat());
        }
        if (type.equals("power")){
            return new Power(leveling.get("scale").getAsFloat());
        }
        return null;
    }

    int getLevel(float xp);

    class Linear implements LevelRule {
        private final float slope;

        public Linear(float amountPerLevel){
            this.slope = amountPerLevel;
        }

        @Override
        public int getLevel(float xp) {
            return (int) Math.floor(xp / this.slope);
        }
    }

    class Power implements LevelRule {
        private final float exponent;

        public Power(float rootBase){
            this.exponent = 1 / rootBase;
        }

        @Override
        public int getLevel(float xp) {
            return (int) Math.floor(Math.pow(xp, this.exponent));
        }
    }
}
