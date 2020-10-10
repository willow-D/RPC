package entity;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@NoArgsConstructor
public class TestUser implements Serializable {

    public String name = "sdfdasd";
    public String age = "18";
    public int[] nums = new int[1024];
    public HashMap<String, String> map = new HashMap<>();
}
