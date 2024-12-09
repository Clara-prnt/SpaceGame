package si.um.feri.clara;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AdventureGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();

        // Load assets
        assetManager.load("assets/down_player.png", Texture.class);
        assetManager.load("assets/right_player.png", Texture.class);
        assetManager.load("assets/left_player.png", Texture.class);
        assetManager.load("assets/up_player.png", Texture.class);
        assetManager.load("assets/villain.png", Texture.class);
        assetManager.load("assets/carrot.png", Texture.class);
        assetManager.load("assets/golden_carrot.png", Texture.class);
        assetManager.load("assets/egg.png", Texture.class);
        assetManager.load("assets/background.jpg", Texture.class);
        assetManager.load("assets/reward.wav", Sound.class);
        assetManager.load("assets/damage.wav", Sound.class);
        assetManager.load("assets/game-over.wav", Sound.class);
        assetManager.load("assets/skin/uiskin.json", Skin.class);
        assetManager.load("assets/intro/background.png", Texture.class);

        assetManager.finishLoading();

        setScreen(new IntroScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getRenderer() {
        return shapeRenderer;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
