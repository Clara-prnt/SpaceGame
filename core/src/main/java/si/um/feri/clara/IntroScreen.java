package si.um.feri.clara;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class IntroScreen extends ScreenAdapter {

    public static final float INTRO_DURATION_IN_SEC = 4f;   // duration of the (intro) animation

    private final AdventureGame game;

    private Texture carrotTexture;
    private Texture bunnyTexture;
    private Texture backgroundTexture;

    private Viewport viewport;

    private float duration = 0f;

    private Stage stage;

    public IntroScreen(AdventureGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());

        carrotTexture = new Texture("assets/carrot.png");
        bunnyTexture = new Texture("assets/down_player.png");
        backgroundTexture = new Texture("assets/intro/background.png");

        stage.addActor(createBunnyAnimation());
        Gdx.input.setInputProcessor(stage);
    }

    private Actor createBunnyAnimation() {
        Image bunny = new Image(bunnyTexture);
        bunny.setPosition(-bunny.getWidth(), 120 + bunny.getHeight() / 2f);

        float centerX = viewport.getWorldWidth() / 2f - bunny.getWidth() / 2f;
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
        carrot.setPosition(viewport.getWorldWidth() / 2f - carrot.getWidth() / 2f, 120 + carrot.getHeight() / 2f);

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
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(65 / 255f, 159 / 255f, 221 / 255f, 0f);

        duration += delta;

        // go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen(game));
        }

        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        carrotTexture.dispose();
        bunnyTexture.dispose();
        backgroundTexture.dispose();
    }
}
