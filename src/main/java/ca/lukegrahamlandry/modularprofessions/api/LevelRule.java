package ca.lukegrahamlandry.modularprofessions.api;

public interface LevelRule {
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

    class Polynomial implements LevelRule {
        private final float exponent;

        public Polynomial(float base){
            this.exponent = 1 / base;
        }

        @Override
        public int getLevel(float xp) {
            return (int) Math.floor(Math.pow(xp, this.exponent));
        }
    }
}
