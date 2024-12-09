package si.um.feri.clara;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents an entity in the game.
 * Every type is a Rectangle but with different properties.
 */
public class Entity {
    public enum Type {
        VILLAIN, CARROT, GOLDEN_CARROT, EGG
    }

    public Rectangle rectangle;
    public Type type;
    public String direction = null;

    public Entity(Rectangle rectangle, Type type) {
        this.rectangle = rectangle;
        this.type = type;
        if (type == Type.VILLAIN) {
            this.direction = "down";
        }
    }

    public Entity(Rectangle rectangle, Type type, String throwingDirection) {
        this.rectangle = rectangle;
        this.type = type;
        if (type == Type.EGG) {
            this.direction = throwingDirection;
        }
    }
}
