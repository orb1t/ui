package ben.ui.widget;

import ben.ui.action.IAction;
import ben.ui.input.mouse.MouseButton;
import ben.ui.input.mouse.MouseListenerAdapter;
import ben.math.PmvMatrix;
import ben.math.Rect;
import ben.math.Vec2i;
import ben.ui.resource.GlResourceManager;
import ben.ui.resource.color.Color;
import ben.ui.renderer.FlatRenderer;
import ben.ui.renderer.TextRenderer;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

import com.jogamp.opengl.GL3;

/**
 * Button.
 */
@ThreadSafe
public class Button extends AbstractWidget {

    /**
     * The padding around the button.
     */
    private static final int PADDING = 5;

    /**
     * The border size in pixels.
     */
    private static final int BORDER = 1;

    /**
     * The height of the button.
     */
    private static final int HEIGHT = TextRenderer.CHARACTER_SIZE + 2 * PADDING;

    /**
     * The background colour of the button.
     */
    @NotNull
    private static final Color BACKGROUND_COLOR = new Color(0.31f, 0.34f, 0.35f);

    /**
     * The border colour of the button.
     */
    @NotNull
    private static final Color BORDER_COLOR = new Color(0.37f, 0.38f, 0.38f);

    /**
     * The highlighted border colour of the button.
     */
    @NotNull
    private static final Color HIGHLIGHTED_BORDER_COLOR = new Color(0.42f, 0.65f, 0.87f);

    /**
     * The text colour.
     */
    @NotNull
    private static final Color TEXT_COLOR = new Color(0.73f, 0.73f, 0.73f);

    /**
     * The disabled text colour.
     */
    @NotNull
    private static final Color DISABLED_TEXT_COLOR = new Color(0.5f, 0.5f, 0.5f);

    /**
     * The text.
     */
    @NotNull
    private final String text;

    /**
     * The background renderer.
     */
    private FlatRenderer backgroundRenderer;

    /**
     * The border renderer.
     */
    private FlatRenderer borderRenderer;

    /**
     * The text renderer.
     */
    private TextRenderer textRenderer;

    /**
     * Constructor.
     * @param name the name of the button
     * @param text the text
     */
    public Button(@NotNull String name, @NotNull String text) {
        super(name);
        this.text = text;
        getMouseHandler().addMouseListener(new MouseListener());
        updateSize();
    }

    @Override
    public String toString() {
        return "Button[text: '" + text + "']";
    }

    @Override
    protected final void initDraw(@NotNull GL3 gl, @NotNull GlResourceManager glResourceManager) {
        backgroundRenderer = new FlatRenderer(gl, glResourceManager, getBgRect(), BACKGROUND_COLOR);
        borderRenderer = new FlatRenderer(gl, glResourceManager, getBorderRect(), BORDER_COLOR);
        textRenderer = new TextRenderer(gl, glResourceManager, text, new Vec2i(PADDING, PADDING), TEXT_COLOR);
    }

    @Override
    protected void updateDraw(@NotNull GL3 gl) {
        backgroundRenderer.setRect(gl, getBgRect());
        borderRenderer.setRect(gl, getBorderRect());
    }

    /**
     * Get the background position and size.
     * @return the rectangle
     */
    private Rect getBgRect() {
        Vec2i bgPos = new Vec2i(BORDER, BORDER);
        Vec2i bgSize = getSize().sub(new Vec2i(2 * BORDER, 2 * BORDER));
        return new Rect(bgPos, bgSize);
    }

    /**
     * Get the border position and size.
     * @return the rectangle
     */
    private Rect getBorderRect() {
        Vec2i borderPos = new Vec2i(0, 0);
        Vec2i borderSize = getSize();
        return new Rect(borderPos, borderSize);
    }

    @Override
    protected final void doDraw(@NotNull GL3 gl, @NotNull PmvMatrix pmvMatrix) {
        borderRenderer.draw(gl, pmvMatrix);
        backgroundRenderer.draw(gl, pmvMatrix);

        textRenderer.setColor(isEnabled() ? TEXT_COLOR : DISABLED_TEXT_COLOR);
        textRenderer.draw(gl, pmvMatrix);
    }

    @Override
    public final void updateSize() {
        int width = text.length() * TextRenderer.CHARACTER_SIZE + 2 * PADDING;
        setSize(new Vec2i(width, HEIGHT));
    }

    @Override
    public void remove(@NotNull GL3 gl) {
        super.remove(gl);
        borderRenderer.remove(gl);
        backgroundRenderer.remove(gl);
        textRenderer.remove(gl);
    }

    /**
     * The Mouse Listener.
     * <p>
     *     Highlights the button on mouse over and executes the action when the button is clicked.
     * </p>
     */
    private class MouseListener extends MouseListenerAdapter {

        @Override
        public void mouseEntered() {
            if (isEnabled()) {
                borderRenderer.setColor(HIGHLIGHTED_BORDER_COLOR);
            }
        }

        @Override
        public void mouseExited() {
            borderRenderer.setColor(BORDER_COLOR);
        }

        @Override
        public void mouseClicked(@NotNull MouseButton button) {
            IAction action = getAction();
            if (action != null) {
                action.execute();
            }
        }
    }
}