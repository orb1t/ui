package ben.ui.renderer;

import ben.math.PmvMatrix;
import ben.ui.resource.GlResourceManager;
import ben.ui.resource.color.Color;
import ben.ui.resource.shader.FlatProgram;
import org.jetbrains.annotations.NotNull;

import com.jogamp.opengl.GL3;

/**
 * Line Renderer.
 * <p>
 *     Renders a 2D or 3D line.
 * </p>
 */
public class LineRenderer {

    @NotNull
    private final VertexArrayObject vertexArrayObject;

    private final FlatProgram program;

    private int numberOfPoints;

    private final int buffer;

    private final int elementsPerVertex;

    private final int lineType;

    @NotNull
    private final Color color;

    /**
     * Constructor.
     * @param gl the OpenGL interface
     * @param glResourceManager the resource manager
     * @param positions the line
     * @param elementsPerVertex either 2 or 3
     * @param lineType the line type, either GL_LINE_STRIP or GL_LINE_LOOP
     * @param color the color of the line
     */
    public LineRenderer(@NotNull GL3 gl, @NotNull GlResourceManager glResourceManager, @NotNull float[] positions,
            int elementsPerVertex, int lineType, @NotNull Color color) {
        assert elementsPerVertex == 2 || elementsPerVertex == 3;
        assert lineType == GL3.GL_LINE_STRIP || lineType == GL3.GL_LINE_LOOP;
        assert positions.length % elementsPerVertex == 0;
        this.elementsPerVertex = elementsPerVertex;
        this.lineType = lineType;
        this.color = color;
        program = glResourceManager.getShaderManager().getProgram(FlatProgram.class);
        vertexArrayObject = new VertexArrayObject(gl);
        numberOfPoints = positions.length / this.elementsPerVertex;
        buffer = vertexArrayObject.addBuffer(gl, FlatProgram.POSITION_LOCATION, positions, this.elementsPerVertex);
    }

    /**
     * Set the positions.
     * @param gl the OpenGL interface
     * @param positions the line
     */
    public final void setPositions(@NotNull GL3 gl, @NotNull float[] positions) {
        numberOfPoints = positions.length / elementsPerVertex;
        vertexArrayObject.updateBuffer(gl, buffer, positions);
    }

    /**
     * Draw.
     * @param gl the OpenGL interface
     * @param pmvMatrix the PMV matrix
     */
    public final void draw(@NotNull GL3 gl, @NotNull PmvMatrix pmvMatrix) {
        program.use(gl);
        program.setPmvMatrix(gl, pmvMatrix);
        program.setColor(gl, color);
        vertexArrayObject.draw(gl, lineType, numberOfPoints);
    }

    /**
     * Remove the renderer.
     * @param gl the OpenGL interface
     */
    public void remove(GL3 gl) {
        vertexArrayObject.remove(gl);
    }
}