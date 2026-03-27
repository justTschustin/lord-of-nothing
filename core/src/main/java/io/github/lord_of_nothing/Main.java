package io.github.lord_of_nothing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.grid.GridRenderer;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private Grid grid;
    private GridRenderer gridRenderer;
    private GridInputHandler gridInputHandler;
    private GameWindow gameWindow;
    private SpriteBatch batch;
    private Texture houseTexture;

    /**
     * Initialisiert die Kernkomponenten, lädt Grafikressourcen und konfiguriert die Eingabeverarbeitung.
     * Stellt sicher, dass alle Render-Systeme und die Spiellogik beim Anwendungsstart bereitstehen.
     */
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        houseTexture = new Texture("buildings/House1.png");

        grid = new Grid();
        gridRenderer = new GridRenderer();
        gridInputHandler = new GridInputHandler(camera, grid);
        gameWindow = new GameWindow(camera, grid);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(gridInputHandler);
    }    @Override
    public void resize(int width, int height) {
        gameWindow.resize(width, height);
        gridInputHandler.updateLayout(gameWindow);
    }
    /**
     * Koordiniert den Zeichenvorgang durch Übergabe der Grafik-Ressourcen an den GridRenderer.
     * Stellt sicher, dass sowohl geometrische Formen als auch Texturen im korrekten Kontext gerendert werden.
     */
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        gridRenderer.render(shapeRenderer, batch, grid, gameWindow, houseTexture, gridInputHandler.isHouseSelected());
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
