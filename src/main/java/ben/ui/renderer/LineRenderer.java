package ben.ui.renderer;

import ben.ui.math.PmvMatrix;
import ben.ui.resource.GlResourceManager;
import ben.ui.resource.color.Color;
import ben.ui.resource.shader.FlatProgram;
import javax.annotation.Nonnull;

import com.jogamp.opengl.GL2;

/**
 * Line Renderer.
 * <p>
 *     Renders a 2D or 3D line.
 * </p>
 */
public final class LineRenderer {

    /**
     * The VAO.
     */
    @Nonnull
    private final VertexArrayObject vertexArrayObject;

    /**
     * The shader program.
     */
    private final FlatProgram program;

    /**
     * The number of points in the line.
     */
    private int numberOfPoints;

    /**
     * The buffer ID.
     */
    private final int buffer;

    /**
     * The number of elements per vertex; 2 for 2D or 3 for 3D.
     */
    private final int elementsPerVertex;

    /**
     * The type of the line; GL2.GL_LINE_STRIP or GL2.GL_LINE_LOOP.
     */
    private final int lineType;

    /**
     * The colour of the line.
     */
    @Nonnull
    private final Color color;

    /**
     * Constructor.
     * @param gl the OpenGL interface
     * @param glResourceManager the resource manager
     * @param positions the line
     * @param elementsPerVertex either 2 or 3
     * @param lineType the line type, either GL_LINES, GL_LINE_STRIP or GL_LINE_LOOP
     * @param color the color of the line
     */
    public LineRenderer(@Nonnull GL2 gl, @Nonnull GlResourceManager glResourceManager, @Nonnull float[] positions,
            int elementsPerVertex, int lineType, @Nonnull Color color) {
        assert elementsPerVertex == 2 || elementsPerVertex == 3;
        assert lineType == GL2.GL_LINES || lineType == GL2.GL_LINE_STRIP || lineType == GL2.GL_LINE_LOOP;
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
    public void setPositions(@Nonnull GL2 gl, @Nonnull float[] positions) {
        numberOfPoints = positions.length / elementsPerVertex;
        vertexArrayObject.updateBuffer(gl, buffer, positions);
    }

    /**
     * Draw.
     * @param gl the OpenGL interface
     * @param pmvMatrix the PMV matrix
     */
    public void draw(@Nonnull GL2 gl, @Nonnull PmvMatrix pmvMatrix) {
        program.use(gl);
        program.setPmvMatrix(gl, pmvMatrix);
        program.setColor(gl, color);
        vertexArrayObject.draw(gl, lineType, numberOfPoints);
    }

    /**
     * Remove the renderer.
     * @param gl the OpenGL interface
     */
    public void remove(GL2 gl) {
        vertexArrayObject.remove(gl);
    }
}
