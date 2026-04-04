package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.ExitButton;
import io.github.lord_of_nothing.button.ResumeButton;
import io.github.lord_of_nothing.events.EventBus;

public class PauseOverlay {
    private static final float BUTTON_WIDTH = 150f;
    private static final float BUTTON_HEIGHT = 40f;
    private static final float BUTTON_GAP = 12f;

    private ResumeButton resumeButton;
    private ExitButton exitButton;

    public void setActive(boolean active) {
        if (resumeButton != null) {
            resumeButton.setEnabled(active);
        }
        if (exitButton != null) {
            exitButton.setEnabled(active);
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, EventBus eventBus) {
        setButtonLayout(eventBus);
        resumeButton.setEnabled(true);
        exitButton.setEnabled(true);

        renderOverlay(shapeRenderer);

        batch.begin();
        resumeButton.render(batch);
        exitButton.render(batch);
        batch.end();
    }

    private void setButtonLayout(EventBus eventBus) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float resumeY = centerY + BUTTON_GAP / 2f;
        float exitY = resumeY - BUTTON_HEIGHT - BUTTON_GAP;

        if (resumeButton == null) {
            resumeButton = new ResumeButton(x, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            resumeButton.setBounds(x, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        if (exitButton == null) {
            exitButton = new ExitButton(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            exitButton.setBounds(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    private void renderOverlay(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
