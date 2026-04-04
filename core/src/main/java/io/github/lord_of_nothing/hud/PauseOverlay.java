package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.ResumeButton;
import io.github.lord_of_nothing.events.EventBus;

public class PauseOverlay {
    private static final float RESUME_BUTTON_WIDTH = 150f;
    private static final float RESUME_BUTTON_HEIGHT = 40f;

    private ResumeButton resumeButton;

    public void setActive(boolean active) {
        if (resumeButton != null) {
            resumeButton.setEnabled(active);
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, EventBus eventBus) {
        setResumeButtonVariables(eventBus);
        resumeButton.setEnabled(true);

        renderOverlay(shapeRenderer);

        batch.begin();
        resumeButton.render(batch);
        batch.end();
    }

    private void setResumeButtonVariables(EventBus eventBus) {
        float x = (Gdx.graphics.getWidth() - RESUME_BUTTON_WIDTH) / 2f;
        float y = (Gdx.graphics.getHeight() - RESUME_BUTTON_HEIGHT) / 2f;

        if (resumeButton == null) {
            resumeButton = new ResumeButton(x, y, RESUME_BUTTON_WIDTH, RESUME_BUTTON_HEIGHT, eventBus);
            return;
        }

        resumeButton.setBounds(x, y, RESUME_BUTTON_WIDTH, RESUME_BUTTON_HEIGHT);
    }

    private void renderOverlay(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.95f);
        shapeRenderer.rect(resumeButton.getX(), resumeButton.getY(), resumeButton.getWidth(), resumeButton.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
