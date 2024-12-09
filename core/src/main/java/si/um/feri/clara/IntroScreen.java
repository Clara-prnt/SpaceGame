package si.um.feri.clara;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class IntroScreen extends ScreenAdapter {

    public static final float INTRO_DURATION_IN_SEC = 4f;   // duration of the (intro) animation

    private final AdventureGame game;
    private final AssetManager assetManager;

    private Stage stage;

    private float duration = 0f;

    Texture carrotTexture;
    Texture bunnyTexture;
    Texture backgroundTexture;

    public IntroScreen(AdventureGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), game.getBatch());

        carrotTexture = assetManager.get("assets/carrot.png", Texture.class);
        bunnyTexture = assetManager.get("assets/down_player.png", Texture.class);
        backgroundTexture = assetManager.get("assets/intro/background.png", Texture.class);

        stage.addActor(createBunnyAnimation());
        Gdx.input.setInputProcessor(stage);
    }

    private Actor createBunnyAnimation() {
        Image bunny = new Image(bunnyTexture);
        bunny.setPosition(-bunny.getWidth(), 120 + bunny.getHeight() / 2f);

        float centerX = stage.getWidth() / 2f - bunny.getWidth() / 2f;
        float floorY = 120 + bunny.getHeight() / 2f;

        bunny.addAction(
            Actions.sequence(
                Actions.run(() -> stage.addActor(createCarrotAnimation())), // Add carrot animation
                Actions.moveTo(centerX - 50, floorY, 2.0f), // Bunny hops to near the center
                Actions.delay(0.5f), // Time before eating the carrot
                Actions.moveTo(centerX, floorY, 0.5f), // Bunny hops to the right
                Actions.delay(1.0f), // Time eating the carrot
                Actions.removeActor() // Remove bunny
            )
        );
        return bunny;
    }

    private Actor createCarrotAnimation() {
        Image carrot = new Image(carrotTexture);
        carrot.setPosition(stage.getWidth() / 2f - carrot.getWidth() / 2f, 120 + carrot.getHeight() / 2f);

        carrot.addAction(
            Actions.sequence(
                Actions.delay(3.0f), // Wait for bunny to hop near the center
                Actions.scaleTo(0, 0, 0.8f), // Carrot "disappears" as bunny eats it
                Actions.removeActor() // Remove carrot
            )
        );

        return carrot;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        duration += delta;

        // go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            Gdx.app.log("IntroScreen", "Switching to MenuScreen");
            hide();
            game.setScreen(new MenuScreen(game));
            return;
        }
        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture,0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        stage.clear();
        stage.dispose();
    }
}
