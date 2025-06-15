package emp.client;

import emp.vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameMain extends JFrame {

    JPanel north_p;
    JLabel start_label, end_label;
    JButton btn;
    JTextField start_F, end_F;

    JTable table;
    String[] c_name = {"사번","이름","입사일","급여","부서명"};
    String[][] data;

    SqlSessionFactory factory;

    public FrameMain(){
        north_p = new JPanel();
        north_p.add(start_label = new JLabel("시작일"));
        north_p.add(start_F = new JTextField(10));
        north_p.add(end_label = new JLabel("종료일"));
        north_p.add(end_F = new JTextField(10));
        north_p.add(btn = new JButton("검색"));
        this.add(north_p, BorderLayout.NORTH);

        this.add(new JScrollPane(table = new JTable()));
        table.setModel(new DefaultTableModel(data,c_name));


        this.setBounds(300,100,500,500);
        this.setVisible(true);

        //종료 이벤트
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //DB연결
        init();

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //검색 버튼을 클릭 했을때
                search();
            }
        });

        start_F.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });

        end_F.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });


    }

    private void search() {

        // 1. 사용자가 입력한 시작일과 종료일을 각각 받아와 앞뒤 공백을 제거한다.
        String str = start_F.getText().trim(); // 시작일 텍스트필드 값
        String ntr = end_F.getText().trim();   // 종료일 텍스트필드 값

        // 2. 두 필드 모두 null이 아닌 경우에만 검색로직을 수행
        if (str != null && ntr != null) {
            // 3. 검색조건을 담을 Map 객체 생성 (키: 파라미터명, 값: 입력값)
            Map<String, String> map = new HashMap<>();
//      System.out.printf("Start : %d"); // 예시로 남겨둔 주석, 사용 안함
            map.put("startDate", str); // 4. 시작일을 맵에 저장
            map.put("endDate", ntr);   // 5. 종료일을 맵에 저장
            // 6. MyBatis의 SqlSession을 오픈 (커넥션 획득)
            SqlSession ss = factory.openSession();

            // 7. emp.search 쿼리를 실행하며, map에 담긴 검색조건을 전달해 결과 리스트를 얻음
            List<EmpVO> list = ss.selectList("emp.search", map);

            // 8. 검색 결과를 테이블에 표시하는 사용자 정의 메서드 호출
            viewTable(list);

            // 9. 데이터베이스 세션 종료 (자원 반환)
            ss.close();

        }

    }

    private  void viewTable(List<EmpVO> list){
        //인자로 받은 List구조를 2차원 배열로 변환한 후 JTable에 표현!
        data = new String[list.size()][c_name.length];
        int i = 0;
        for (EmpVO vo : list) {
            data[i][0] = vo.getEmpno();
            data[i][1] = vo.getEname();
            data[i][2] = vo.getHiredate();
            data[i][3] = vo.getSal();
            data[i][4] = vo.getDname();
            i++;
        }//for종료
        table.setModel(new DefaultTableModel(data, c_name));

    }


    private void init(){

        try{
            Reader r = Resources.getResourceAsReader("emp/config/conf.xml");
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            this.setTitle("준비완료");

            SqlSession ss = factory.openSession();
            Map<String, String> map = new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FrameMain();

    }
}
