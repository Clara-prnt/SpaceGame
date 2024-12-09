package si.um.feri.clara;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;
import java.util.Random;


public class GameScreen extends ScreenAdapter {

    //what is that ?
    private final SpaceGame game;

    public GameScreen(SpaceGame game) {
        this.game = game;
    }

    private SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    private Texture characterImg;
    private Texture villainImg;
    private Texture carrotImg;
    private Texture goldenCarrotImg;
    private Texture eggImg;
    private Texture backgroundImg;

    private Sound rewardSound;
    private Sound damageSound;
    private Sound gameOverSound;

    private String scoreBoard;
    private int score = 0;
    private BitmapFont font;

    private Rectangle player;
    private Array<Entity> entities;

    private float currentHealth = 100f;
    private boolean isGameOver = false;

    private float villainSpawnTimer = 0f;
    private float carrotSpawnTimer = 0f;
    private String throwingDirection = "up";

    private static final float PLAYER_SPEED = 200f;
    private static final float VILLAIN_SPAWN_INTERVAL = 3f;
    private static final float CARROT_SPAWN_INTERVAL = 2f;
    private static final float VILLAIN_SPEED = 150f;
    private static final float EGG_SPEED = 300f;

    private static final float BAR_WIDTH = 200f;
    private static final float BAR_HEIGHT = 20f;
    private static final float BAR_PADDING = 10f;
    private static final float PADDING = 20f;

    /**
     * Called when the Game is first created.
     * Initializes the game objects and sounds and initializes the game.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Creating the 'character' and 'objects' images
        characterImg = new Texture("./assets/big_down_player.png");
        villainImg = new Texture("./assets/big_villain.png");
        carrotImg = new Texture("./assets/big_carrot.png");
        goldenCarrotImg = new Texture("./assets/golden_carrot.png");
        eggImg = new Texture("./assets/egg.png");
        backgroundImg = new Texture("./assets/background.jpg");

        rewardSound = Gdx.audio.newSound(Gdx.files.internal("./assets/reward.wav"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("./assets/damage.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("./assets/game-over.wav"));

        font = new BitmapFont();
        scoreBoard = "Score: " + score;

        entities = new Array<>();
        createCharacter();
        render();
    }

    /**
     * Creates the character object.
     */
    public void createCharacter() {
        characterImg = new Texture("./assets/big_down_player.png");
        player = new Rectangle(Gdx.graphics.getWidth() / 2f - characterImg.getWidth() / 2f,
            PADDING, characterImg.getWidth(), characterImg.getHeight());

    }

    /**
     * Create a carrot object and adds it to the entities array.
     */
    public void spawnCarrot() {
        Rectangle carrot = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - carrotImg.getWidth() - PADDING),
            MathUtils.random(PADDING, Gdx.graphics.getHeight() - carrotImg.getHeight() - PADDING), carrotImg.getWidth(), carrotImg.getHeight());
        entities.add(new Entity(carrot, Entity.Type.CARROT));
    }

    /**
     * Create a golden carrot (booster) object and adds it to the entities array.
     */
    public void spawnGoldenCarrot() {
        Rectangle goldenCarrot = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - carrotImg.getWidth() - PADDING),
            MathUtils.random(PADDING, Gdx.graphics.getHeight() - carrotImg.getHeight() - PADDING), carrotImg.getWidth(), carrotImg.getHeight());
        entities.add(new Entity(goldenCarrot, Entity.Type.GOLDEN_CARROT));
    }

    /**
     * Create a villain object and adds it to the entities array.
     */
    public void spawnVillain() {
        Rectangle villain = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - villainImg.getWidth() - PADDING),
            Gdx.graphics.getHeight(), villainImg.getWidth(), villainImg.getHeight());
        entities.add(new Entity(villain, Entity.Type.VILLAIN));
    }

    /**
     * Create an egg object and adds it to the entities array.
     * The egg is thrown in the direction the player is facing.
     */
    public void spawnEgg() {
        Rectangle egg = new Rectangle(player.x + player.width / 2 - (float) eggImg.getWidth() / 2,
            player.y + player.height / 2 - (float) eggImg.getHeight() / 2, eggImg.getWidth(), eggImg.getHeight());
        entities.add(new Entity(egg, Entity.Type.EGG, throwingDirection));
    }

    /**
     * Called when the screen should render itself.
     * Handles input, updates the game state, and renders the game objects.
     */
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        handleInput(Gdx.graphics.getDeltaTime());

        if (!isGameOver) {
            update(Gdx.graphics.getDeltaTime());
        }

        batch.begin();
        drawBackground();
        drawPlayer();
        drawEntities();
        drawScoreBoard();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawHealthBar();
        shapeRenderer.end();

        if (isGameOver) {
            batch.begin();
            drawGameOver();
            batch.end();
        }
    }

    /**
     * Handles the input from the user.
     * The player can move the character using the arrow keys or WASD keys.
     * The player can throw an egg using the left mouse button.
     * The player can restart the game by pressing the space key when GAME OVER.
     * The player can exit the game by pressing the escape key.
     * @param delta The time in seconds since the last render (1 frame).
     */
    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && isGameOver) {
            createCharacter();
            entities.clear();
            currentHealth = 100f;
            score = 0;
            scoreBoard = "Score: " + score;
            isGameOver = false;
        }

        if (currentHealth > 0f) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
                characterImg = new Texture("./assets/big_right_player.png");
                player.x += PLAYER_SPEED * delta;
                if(player.x > Gdx.graphics.getWidth() - player.getWidth() - PADDING) {
                    player.x = Gdx.graphics.getWidth() - player.getWidth() - PADDING;
                }
                throwingDirection = "right";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                characterImg = new Texture("./assets/big_left_player.png");
                player.x -= PLAYER_SPEED * delta;
                if(player.x < player.getWidth()) {
                    player.x = player.getWidth();
                }
                throwingDirection = "left";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                characterImg = new Texture("./assets/big_up_player.png");
                player.y += PLAYER_SPEED * delta;
                if(player.y > Gdx.graphics.getHeight() - player.getHeight() - PADDING) {
                    player.y = Gdx.graphics.getHeight() - player.getHeight() - PADDING;
                }
                throwingDirection = "up";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                characterImg = new Texture("./assets/big_down_player.png");
                player.y -= PLAYER_SPEED * delta;
                if(player.y < player.getHeight()) {
                    player.y = player.getHeight();
                }
                throwingDirection = "down";
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                spawnEgg();
            }
        }
    }

    /**
     * Updates the game state.
     * Spawns villains and carrots at regular intervals.
     * Updates the position of the entities.
     * Checks for collisions between entities.
     * Updates the score and health.
     * Ends the game if the health reaches 0.
     * @param delta The time in seconds since the last render (1 frame).
     */
    private void update(float delta) {
        Random rand = new Random();
        int roll = rand.nextInt(10);
        if (currentHealth > 0f) {
            villainSpawnTimer += delta;
            carrotSpawnTimer += delta;
            if (villainSpawnTimer > VILLAIN_SPAWN_INTERVAL) {
                spawnVillain();
                villainSpawnTimer = 0f;
            }
            if (carrotSpawnTimer > CARROT_SPAWN_INTERVAL) {
                if (roll == 1){
                    spawnGoldenCarrot();
                } else {
                    spawnCarrot();
                }
                carrotSpawnTimer = 0f;
            }
        }

        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            switch (entity.type) {
                case VILLAIN:
                    entity.rectangle.y -= VILLAIN_SPEED * delta;
                    if (entity.rectangle.overlaps(player)) {
                        iterator.remove();
                        currentHealth -= 10f;
                        scoreBoard = "Score: " + score;
                        if (currentHealth <= 0f) {
                            gameOver();
                        }
                        damageSound.play();
                    }

                    if (entity.rectangle.y + entity.rectangle.height < 0f) {
                        iterator.remove();
                    }
                    break;
                case CARROT:
                    if (entity.rectangle.overlaps(player)) {
                        rewardSound.play();
                        score++;
                        scoreBoard = "Score: " + score;
                        iterator.remove();
                    }
                    break;
                case GOLDEN_CARROT:
                    if (entity.rectangle.overlaps(player)) {
                        rewardSound.play();
                        if (currentHealth <= 90f){
                            currentHealth += 10f;
                            score++;
                        } else {
                            score += 10;
                        }
                        scoreBoard = "Score: " + score;
                        iterator.remove();
                    }
                    break;
                case EGG:
                    switch (entity.direction) {
                        case "up":
                            entity.rectangle.y += EGG_SPEED * delta;
                            break;
                        case "down":
                            entity.rectangle.y -= EGG_SPEED * delta;
                            break;
                        case "left":
                            entity.rectangle.x -= EGG_SPEED * delta;
                            break;
                        case "right":
                            entity.rectangle.x += EGG_SPEED * delta;
                            break;
                    }

                    // Check if the egg hits a villain + cannot use iterator inside another iterator
                    for (int i = 0; i < entities.size; i++) {
                        Entity otherEntity = entities.get(i);
                        if (otherEntity.type == Entity.Type.VILLAIN && entity.rectangle.overlaps(otherEntity.rectangle)) {
                            iterator.remove();
                            entities.removeValue(otherEntity, true);
                            score += 5;
                            scoreBoard = "Score: " + score;
                            rewardSound.play();
                        }
                    }

                    if (entity.rectangle.y + entity.rectangle.height < 0f || entity.rectangle.y > Gdx.graphics.getHeight() ||
                        entity.rectangle.x + entity.rectangle.width < 0f || entity.rectangle.x > Gdx.graphics.getWidth()) {
                        iterator.remove();
                    }
                    break;
            }
        }
    }

    /**
     * Draws the background image.
     */
    private void drawBackground(){
        batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Draws the entities on the screen.
     * All entities are seen as rectangles.
     */
    private void drawEntities() {
        for (Entity entity : entities) {
            switch (entity.type) {
                case VILLAIN:
                    batch.draw(villainImg, entity.rectangle.x, entity.rectangle.y, entity.rectangle.width, entity.rectangle.height);
                    break;
                case CARROT:
                    batch.draw(carrotImg, entity.rectangle.x, entity.rectangle.y, entity.rectangle.width, entity.rectangle.height);
                    break;
                case GOLDEN_CARROT:
                    batch.draw(goldenCarrotImg, entity.rectangle.x, entity.rectangle.y, entity.rectangle.width, entity.rectangle.height);
                    break;
                case EGG:
                    batch.draw(eggImg, entity.rectangle.x, entity.rectangle.y, entity.rectangle.width, entity.rectangle.height);
                    break;
            }
        }
    }


    /**
     * Draws the player character on the screen.
     */
    private void drawPlayer() {
        batch.draw(characterImg, player.x, player.y, player.width, player.height);
    }

    /**
     * Draws the score board on the screen.
     * Initializes the font and setting the color as black.
     */
    public void drawScoreBoard() {
        font.getData().setScale(2);
        font.setColor(0, 0, 0, 1);
        font.draw(batch, scoreBoard, 10, Gdx.graphics.getHeight() - 10);
    }

    /**
     * Draws the health bar on the screen.
     * The health bar is a rectangle.
     * When the health lowers, the green part of the bar decreases, letting the red part appears.
     */
    private void drawHealthBar() {
        float healthPercentage = currentHealth / 100f;
        shapeRenderer.setColor(1, 0, 0, 1); // Red
        shapeRenderer.rect(BAR_PADDING, Gdx.graphics.getHeight() - BAR_PADDING - BAR_HEIGHT - font.getLineHeight(), BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.setColor(0, 1, 0, 1); // Green
        shapeRenderer.rect(BAR_PADDING, Gdx.graphics.getHeight() - BAR_PADDING - BAR_HEIGHT - font.getLineHeight(), BAR_WIDTH * healthPercentage, BAR_HEIGHT);
    }

    /**
     * Draws the 'game over' message on the screen.
     * Initializes the font and setting the color as red.
     */
    private void drawGameOver() {
        font.getData().setScale(2);
        font.setColor(1, 0, 0, 1);
        font.draw(batch, "Game Over",
            Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
    }

    /**
     * Called when the player loses all health.
     * Plays the game over sound and sets the game state to 'game over'.
     */
    public void gameOver() {
        isGameOver = true;
        gameOverSound.play();
    }

    /**
     * Called when the Game is destroyed.
     * Disposes of the game objects and sounds.
     */
    @Override
    public void dispose() {
        batch.dispose();
        characterImg.dispose();
        villainImg.dispose();
        carrotImg.dispose();
        goldenCarrotImg.dispose();
        eggImg.dispose();
        backgroundImg.dispose();
        rewardSound.dispose();
        damageSound.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void hide() {
        dispose();
    }
}
