package com.zygon.rl.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zygon.rl.common.controller.GameControllerImpl;
import com.zygon.rl.context.gdx.GDXRender;
import com.zygon.rl.core.controller.GameController;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.view.GameRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 */
public class GameScreen extends ScreenAdapter {

    private final Supplier<Game> gameSupplier;
    private final Consumer<Game> gameConsumer;
    private final GameController gameController = new GameControllerImpl();

    private GameRenderer gameRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private Music music;
    private boolean dirtyGame = true;

    GameScreen(Supplier<Game> gameSupplier, Consumer<Game> gameConsumer) {
        this.gameSupplier = gameSupplier;
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        music = Gdx.audio.newMusic(Gdx.files.internal("media/Punch Deck - Oppressive Ambiance.wav"));
        music.setLooping(true);
        music.setVolume(0.05f);
        music.play();

        camera = new OrthographicCamera(w, h);
        camera.update();

        spriteBatch = new SpriteBatch();
        gameRenderer = new GDXRender(gameSupplier, spriteBatch, camera);
    }

    @Override
    public void render(float delta) {

        // Simple zoom functionality
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
            camera.zoom -= 0.1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
            camera.zoom += 0.1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.HOME)) {
            camera.zoom = 1.0f;
        }
        // Simple volume
        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            music.setVolume(music.getVolume() + .05f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            music.setVolume(music.getVolume() - .05f);
        }

        if (dirtyGame) {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();

            spriteBatch.setProjectionMatrix(camera.combined);

            spriteBatch.begin();
            gameRenderer.render(gameSupplier.get());
            spriteBatch.end();

            dirtyGame = false;
        }
        // This will update the game state
        processInput();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        music.dispose();
        spriteBatch.dispose();
    }

    private void processInput() {
        Game game = gameSupplier.get();

        com.zygon.rl.core.model.Input input = game.getInputSupplier().get();
        if (!input.isUnknown()) {
            com.zygon.rl.core.model.Action action = gameController.convertInput(gameSupplier.get(), input);
            if (action != null) {
                gameConsumer.accept(gameController.handleAction(gameSupplier.get(), action));
            } else {
                gameConsumer.accept(gameController.handleInvalidInput(gameSupplier.get(), input));
            }
            dirtyGame = true;
        }
    }
}
