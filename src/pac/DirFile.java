package pac;

import java.io.File;

/**
 * Created by coder on 07.03.16.
 */
public class DirFile {

    public static void main(String[] args){
        boolean isDirCreated = new File("./test/").mkdir();
        System.out.println((isDirCreated)?"OK":"Fail");
    }
}
