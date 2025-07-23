import java.util.HashSet;

public class Main {

    String str = "abcabcab";

    public static void main(Strings[] args){
        HashSet<Character> set = new HashSet<>();

        int n = str.length();
        int i =0;
        int si =0, ei =0;
        String ans = "";
        while(si<n){
            while(!set.contains(str.charAt(ei))){
                set.add(str.charAt(ei));
                ei++;
            }
            if(ei-si > ans.length()){
                ans = str.substring(si,ei);
            }
            si = ei;
            set.clear();
        }
        System.out.println(ans);
    }
}
