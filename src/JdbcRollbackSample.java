
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcRollbackSample {

	private static final String URL = "jdbc:mysql://localhost:3306/database01?user=user01&"
			+ "password=password01&useSSL=false&allowPublicKeyRetrieval=true";

	public static void main(String[] args) throws ClassNotFoundException {

		Connection connection = null;

		try {

			// コネクション取得処理
			connection = DriverManager.getConnection(URL);

			// 1. 自動コミットさせない設定
			connection.setAutoCommit(false);

			// ↓ユーザーAさんのお金を1,000円減らすSQLを発行
			PreparedStatement statement = connection.prepareStatement("update user2 set money = money - ? where id = ?");

			// プレースホルダにパラメータをセット
			statement.setInt(1, 1000);
			statement.setLong(2, 1);

			// SQLの実行
			statement.executeUpdate();

			// ↓ユーザーBさんのお金を1,000円増やすSQLを発行(わざと set を sets に書き換えた)
			statement = connection.prepareStatement("update user2 sets money = money + ? where id = ?");

			// プレースホルダにパラメータをセット
			statement.setInt(1, 1000);
			statement.setLong(2, 2);

			// SQLの実行
			statement.executeUpdate();

			// 2. 全てのSQLが成功したので、コミット処理
			connection.commit();
			
			System.out.println("正常にコミットが実行されました");

		} catch (Exception e) {
			// 3. 例外が発生したのでロールバック処理
			try {
				connection.rollback();
				System.out.println("コミットに失敗しロールバックが実行されました");
			} catch (SQLException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}

	}

}