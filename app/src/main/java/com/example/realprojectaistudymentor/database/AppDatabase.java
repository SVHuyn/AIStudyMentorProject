package com.example.realprojectaistudymentor.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.realprojectaistudymentor.database.dao.AnswerDao;
import com.example.realprojectaistudymentor.database.dao.BadgeDao;
import com.example.realprojectaistudymentor.database.dao.QuestionDao;
import com.example.realprojectaistudymentor.database.dao.QuizAttemptDao;
import com.example.realprojectaistudymentor.database.dao.UserDao;
import com.example.realprojectaistudymentor.database.entity.AnswerEntity;
import com.example.realprojectaistudymentor.database.entity.BadgeEntity;
import com.example.realprojectaistudymentor.database.entity.QuestionEntity;
import com.example.realprojectaistudymentor.database.entity.QuizAttemptEntity;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.utils.Constants;

@Database(
        entities = {
                UserEntity.class,
                QuestionEntity.class,
                AnswerEntity.class,
                QuizAttemptEntity.class,
                BadgeEntity.class
        },
        version = Constants.DB_VERSION,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    // DAOs — mỗi member tự dùng cái của mình
    public abstract UserDao userDao();
    public abstract QuestionDao questionDao();
    public abstract AnswerDao answerDao();
    public abstract QuizAttemptDao quizAttemptDao();
    public abstract BadgeDao badgeDao();

    // Singleton — chỉ tạo 1 instance duy nhất trong toàn app
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DB_NAME
                    )
                    .allowMainThreadQueries() // đơn giản cho intern, production nên dùng AsyncTask/LiveData
                    .fallbackToDestructiveMigration() // xoá DB khi đổi version
                    .build();
        }
        return instance;
    }
}
