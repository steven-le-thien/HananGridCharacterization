import java.util.Arrays;


/**
 * Created by lethien_96 on 2/23/17.
 */
class Vertex {
    int dim;
    int[] tuple;

    Vertex(int... x) {
        this.dim = x.length;
        this.tuple = x;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Vertex && ((Vertex) obj).dim == this.dim && Arrays.equals(((Vertex) obj).tuple, this.tuple));
    }
}
