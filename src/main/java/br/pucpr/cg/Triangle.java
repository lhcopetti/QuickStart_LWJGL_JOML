package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import br.pucpr.mage.Scene;
import br.pucpr.mage.Window;


public class Triangle implements Scene {
	
	private static final String VERTEX_SHADER = 
			"#version 330\n" + 
			"in vec2 aPosition;\n" +
			"void main() {\n" +
			"    gl_Position = vec4(aPosition, 0.0, 1.0);\n" +
			"}";

	private static final String FRAGMENT_SHADER = 
			"#version 330\n" +
			"out vec4 out_color;\n" +
			"void main() {\n" +
			"    out_color = vec4(1.0, 1.0, 0.0, 1.0);\n" +
			"}";
	
	private int vao;
	private int positions;
	private int shader;

	@Override
	public void init() {
		glClearColor(0.0f, 0.0f, 0.2f, 1.0f);
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		float[] vertexData =
				{ /* Sentido anti-hor�rio */
						0.0f, 0.5f,
						-0.5f, -0.5f,
						0.5f, -0.5f
				};
		
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		positionBuffer.put(vertexData).flip();
		
		positions = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		
		int vertex = compileShader (GL_VERTEX_SHADER, VERTEX_SHADER);
		int fragment = compileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
		
		shader = linkProgram(vertex, fragment);
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glBindVertexArray(0);
	}
	
	private int compileShader(int shaderType, String code)
	{
		
		int shader = glCreateShader(shaderType);
		glShaderSource(shader, code);
		glCompileShader(shader);
		
		if (glGetShaderi(shaderType,  GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("Unable to compile Shader: " + glGetShaderInfoLog(shader));
		}
		
		return shader;
	}
	
	public int linkProgram(int ... shaders)
	{
		int program = glCreateProgram();
		for (int shader : shaders)
			glAttachShader(program, shader);
		
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
		{
			throw new RuntimeException("Unable to link shaders: " + glGetProgramInfoLog(program));
		}
	
		for(int shader: shaders)
		{
			glDetachShader(program, shader);
			glDeleteShader(shader);
		}
		
		return program;
	}
	

	@Override
	public void update(float secs) {
	}

	@Override
	public void draw() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		glUseProgram(shader);
		glBindVertexArray(vao);
		
		int aPosition = glGetAttribLocation(shader, "aPosition");
		glEnableVertexAttribArray(aPosition);
		
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);
		
		glDrawArrays(GL_TRIANGLES, 0, 3);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(aPosition);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new Triangle()).show();
	}

	@Override
	public void keyPressed(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
			glfwSetWindowShouldClose(window, GLFW_TRUE); // We will detect this
															// in our rendering
															// loop
	}

}
