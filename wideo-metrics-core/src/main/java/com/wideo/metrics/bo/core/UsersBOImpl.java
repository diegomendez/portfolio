package com.wideo.metrics.bo.core;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.dal.mysql.UsersDal;
import com.wideo.metrics.model.processor.stats.DashboardStats;

@Service
@ComponentScan("com.wideo.metrics")
public class UsersBOImpl implements UsersBO {

    @Autowired
    UsersDal usersDal;
    private static final Map<String, String> userCategoryNamesMap;
    static {
        Map<String, String> userCategoryNames = new HashMap<String, String>();

        userCategoryNames.put("1", "Start Up");
        userCategoryNames.put("2", "Small Business");
        userCategoryNames.put("5", "Corporate");
        userCategoryNames.put("7", "Video Artist/Animator");
        userCategoryNames.put("8", "Teacher");
        userCategoryNames.put("9", "Student");
        userCategoryNames.put("10", "Other");
        userCategoryNames.put("11", "Marketing Agency");
        userCategoryNames.put("12", "Marketing Department");
        userCategoryNames.put("13", "E-Commerce");
        userCategoryNames.put("14", "Youtuber");

        userCategoryNamesMap = Collections.unmodifiableMap(userCategoryNames);
    }

    @Override
    public Long getSignupsByDate(Date startDate, Date endDate,
            List<String> userCategories) {
        return usersDal.getSignupsByDate(startDate, endDate, userCategories);

    }

    @Override
    public List<DashboardStats> getSignupsByUserCategoryByDate(Date startDate,
            Date endDate) {
        List<DashboardStats> signupsByUserCategory = usersDal
                .getSignupsByUserCategoryByDate(startDate, endDate);
        for (DashboardStats userCategorySignups : signupsByUserCategory) {
            userCategorySignups.setName(userCategoryNamesMap
                    .get(userCategorySignups.getName()));
        }
        return signupsByUserCategory;
    }

    @Override
    public Integer getUsersSignedUp(Date startDate, Date endDate,
            List<Long> userIDs, List<String> userCategories) {
        
        List<Long> usersSignedUp = usersDal.getUsersSignedUp(startDate,
                endDate, userIDs, userCategories);
        return usersSignedUp.size();
    }
}
