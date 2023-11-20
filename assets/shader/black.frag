#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;  // Zeit-Uniform für Transparenzanimation

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);

    // Ändere die Transparenz basierend auf der Zeit
    float alpha = 0.5 + 0.5 * sin(u_time);

    gl_FragColor = texColor * alpha;
}