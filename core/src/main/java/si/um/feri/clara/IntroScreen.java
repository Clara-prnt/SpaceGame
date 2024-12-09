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

    public static final float INTRO_DURATION_IN_SEC = 3f;   // duration of the (intro) animation

    private final SpaceGame game;

    private Texture keyTexture;
    private Texture keyholeTexture;

    private Viewport viewport;

    private float duration = 0f;

    private Stage stage;

    public IntroScreen(SpaceGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());

        keyTexture = new Texture("assets/intro/key.png");
        keyholeTexture = new Texture("assets/intro/keyhole.png");

        stage.addActor(createKeyhole());
        stage.addActor(createAnimation());
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
    }

    private Actor createKeyhole() {
        Image keyhole = new Image(keyholeTexture);
        // position the image to the center of the window
        keyhole.setPosition(viewport.getWorldWidth() / 2f - keyhole.getWidth() / 2f,
            viewport.getWorldHeight() / 2f - keyhole.getHeight() / 2f);
        return keyhole;
    }

    private Actor createAnimation() {
        Image key = new Image(keyTexture);

        // set positions x, y to center the image to the center of the window
        float posX = (viewport.getWorldWidth() / 2f) - key.getWidth() / 2f;
        float posY = (viewport.getWorldHeight() / 2f) - key.getHeight() / 2f;

        key.setOrigin(Align.center);
        key.addAction(
            /* animationDuration = Actions.sequence + Actions.rotateBy + Actions.scaleTo
                                 = 1.5 + 1 + 0.5 = 3 sec */
            Actions.sequence(
                Actions.parallel(
                    Actions.rotateBy(1080, 1.5f),   // rotate the image three times
                    Actions.moveTo(posX, posY, 1.5f)   // // move image to the center of the window
                ),
                Actions.rotateBy(-360, 1),  // rotate the image for 360 degrees to the left side
                Actions.scaleTo(0, 0, 0.5f),    // "minimize"/"hide" image
                Actions.removeActor()   // // remove image
            )
        );

        return key;
    }
}
