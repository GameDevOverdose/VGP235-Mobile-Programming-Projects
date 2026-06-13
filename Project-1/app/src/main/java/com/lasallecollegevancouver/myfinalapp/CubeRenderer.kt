package com.lasallecollegevancouver.myfinalapp

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.*
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.*
import android.opengl.Matrix
import kotlin.math.*

class CubeRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // =========================================================
    // FACE SYSTEM
    // =========================================================
    enum class Face { FRONT, RIGHT, BACK, LEFT, TOP, BOTTOM }

    private var currentFace = Face.FRONT
    private var targetRotY = 0f

    var launcher: ((String) -> Unit)? = null

    // =========================================================
    // ROTATION
    // =========================================================
    private var rotX = 0f
    private var rotY = 0f

    private var lastX = 0f
    private var lastY = 0f

    // =========================================================
    // MESH (REAL CUBE - 36 VERTICES)
    // =========================================================

    private val vertices = floatArrayOf(

        // FRONT
        -1f,-1f, 1f,   1f,-1f, 1f,   1f, 1f, 1f,
        -1f,-1f, 1f,   1f, 1f, 1f,  -1f, 1f, 1f,

        // BACK
        1f,-1f,-1f,  -1f,-1f,-1f,  -1f, 1f,-1f,
        1f,-1f,-1f,  -1f, 1f,-1f,   1f, 1f,-1f,

        // LEFT
        -1f,-1f,-1f,  -1f,-1f, 1f,  -1f, 1f, 1f,
        -1f,-1f,-1f,  -1f, 1f, 1f,  -1f, 1f,-1f,

        // RIGHT
        1f,-1f, 1f,   1f,-1f,-1f,   1f, 1f,-1f,
        1f,-1f, 1f,   1f, 1f,-1f,   1f, 1f, 1f,

        // TOP
        -1f, 1f, 1f,   1f, 1f, 1f,   1f, 1f,-1f,
        -1f, 1f, 1f,   1f, 1f,-1f,  -1f, 1f,-1f,

        // BOTTOM
        -1f,-1f,-1f,   1f,-1f,-1f,   1f,-1f, 1f,
        -1f,-1f,-1f,   1f,-1f, 1f,  -1f,-1f, 1f
    )

    private val texCoords = floatArrayOf(
        // same UV for every face
        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f,

        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f,

        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f,

        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f,

        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f,

        0f,0f, 1f,0f, 1f,1f,
        0f,0f, 1f,1f, 0f,1f
    )

    private val vb: FloatBuffer =
        ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply { put(vertices); position(0) }

    private val tb: FloatBuffer =
        ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply { put(texCoords); position(0) }

    // =========================================================
    // MATRICES
    // =========================================================
    private val model = FloatArray(16)
    private val view = FloatArray(16)
    private val proj = FloatArray(16)
    private val mvp = FloatArray(16)

    private var program = 0

    private val textures = IntArray(6)

    // =========================================================
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.08f, 0.10f, 0.14f, 1f) // AAA dark UI background

        program = createProgram()

        //textures[0] = load(R.drawable.tic_tac_toe)
        //textures[1] = load(R.drawable.minesweeper)
        //textures[2] = load(R.drawable.snake)
        //textures[3] = load(R.drawable.memory)
        //textures[4] = load(R.drawable.rps)
        //textures[5] = load(R.drawable.ai)
    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        GLES20.glViewport(0, 0, w, h)
        val ratio = w.toFloat() / h
        Matrix.frustumM(proj, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        Matrix.setLookAtM(view, 0,
            0f, 0f, 6f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        // smooth cinematic snap (AAA easing)
        rotY += (targetRotY - rotY) * 0.12f

        // subtle idle motion (AAA feel)
        rotY += 0.15f

        Matrix.setIdentityM(model, 0)
        Matrix.rotateM(model, 0, rotX, 1f, 0f, 0f)
        Matrix.rotateM(model, 0, rotY, 0f, 1f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, view, 0, model, 0)
        Matrix.multiplyMM(mvp, 0, proj, 0, temp, 0)

        val mvpLoc = GLES20.glGetUniformLocation(program, "uMVP")
        val posLoc = GLES20.glGetAttribLocation(program, "pos")
        val texLoc = GLES20.glGetAttribLocation(program, "tex")

        GLES20.glUniformMatrix4fv(mvpLoc, 1, false, mvp, 0)

        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, vb)

        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, tb)

        // bind FRONT face texture (we’ll extend per-face shading next step)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 3)

        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)
    }

    // =========================================================
    // TOUCH
    // =========================================================
    fun onTouch(e: MotionEvent) {
        when (e.action) {

            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - lastX
                val dy = e.y - lastY

                rotY += dx * 0.4f
                rotX += dy * 0.4f
            }

            MotionEvent.ACTION_UP -> {
                snap()
                trigger()
            }
        }

        lastX = e.x
        lastY = e.y
    }

    // =========================================================
    private fun snap() {

        val y = (rotY % 360 + 360) % 360

        currentFace = when {
            y < 45 || y > 315 -> Face.FRONT
            y < 135 -> Face.RIGHT
            y < 225 -> Face.BACK
            else -> Face.LEFT
        }

        targetRotY = when (currentFace) {
            Face.FRONT -> 0f
            Face.RIGHT -> 90f
            Face.BACK -> 180f
            Face.LEFT -> 270f
            else -> 0f
        }
    }

    private fun trigger() {
        launcher?.invoke(
            when (currentFace) {
                Face.FRONT -> "tictactoe"
                Face.RIGHT -> "minesweeper"
                Face.BACK -> "snake"
                Face.LEFT -> "memory"
                Face.TOP -> "rps"
                Face.BOTTOM -> "ai"
            }
        )
    }

    // =========================================================
    private fun load(resId: Int): Int {
        val tex = IntArray(1)
        GLES20.glGenTextures(1, tex, 0)

        val bmp = BitmapFactory.decodeResource(context.resources, resId)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0])
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        bmp.recycle()
        return tex[0]
    }

    // =========================================================
    private fun createProgram(): Int {

        val vs = """
            attribute vec4 pos;
            attribute vec2 tex;
            uniform mat4 uMVP;
            varying vec2 vTex;

            void main() {
                gl_Position = uMVP * pos;
                vTex = tex;
            }
        """

        val fs = """
            precision mediump float;
            varying vec2 vTex;
            uniform sampler2D tex0;

            void main() {
                gl_FragColor = texture2D(tex0, vTex);
            }
        """

        val v = compile(GLES20.GL_VERTEX_SHADER, vs)
        val f = compile(GLES20.GL_FRAGMENT_SHADER, fs)

        return GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, v)
            GLES20.glAttachShader(it, f)
            GLES20.glLinkProgram(it)
        }
    }

    private fun compile(type: Int, src: String): Int {
        return GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, src)
            GLES20.glCompileShader(it)
        }
    }
}