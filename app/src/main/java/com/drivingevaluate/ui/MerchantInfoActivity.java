package com.drivingevaluate.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.drivingevaluate.R;
import com.drivingevaluate.adapter.CoachHorizontalAdapter;
import com.drivingevaluate.adapter.CourseAdapter;
import com.drivingevaluate.model.Coach;
import com.drivingevaluate.model.Course;
import com.drivingevaluate.model.Evaluation;
import com.drivingevaluate.model.Merchant;
import com.drivingevaluate.net.GetCoachListRequester;
import com.drivingevaluate.net.GetEvaluationListRequester;
import com.drivingevaluate.net.GetMerchantDetailRequester;
import com.drivingevaluate.net.component.RequestErrorHandler;
import com.drivingevaluate.ui.base.Yat3sActivity;
import com.drivingevaluate.util.DateUtils;
import com.drivingevaluate.view.FullyLinearLayoutManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MerchantInfoActivity extends Yat3sActivity implements OnClickListener {
    private LinearLayout navigateLl, evaluationLl,evaluationPreLl,loading;
    private Button btnApply, btnConsult, moreCoachBtn,moreEvaluationBtn;
    private TextView tvName, merchantIntroTv, tvCoachAmount, studentAmountTextView, gradeTextView,addressTv,evaluationMerchantTtv;
    private TextView timeGradeTv,placeGradeTv,serviceGradeTv;
    private RatingBar timeGradeRb,placeGradeRb,serviceGradeRb;
    private RecyclerView coachRv;
    private RecyclerView courseRv;
    private CoachHorizontalAdapter coachHorizontalAdapter;
    private CourseAdapter courseAdapter;
    private ImageButton btnMap;
    private List<Coach> coaches = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private int merchantId;
    private Merchant merchant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackTitleBar();
        setContentView(R.layout.activity_merchant_info);

        initView();
        initEvent();

        getData();
    }

    private void refreshView() {
        setTitleBarTitle(merchant.getSname());
        tvName.setText(merchant.getSname());
        studentAmountTextView.setText("报名人数:" + merchant.getSellCount());
        addressTv.setText(merchant.getSaddress());
        merchantIntroTv.setText(merchant.getSintroduction());
        tvCoachAmount.setText("精品教练(" + coaches.size() + ")");
        moreCoachBtn.setText("查看全部" + coaches.size() + "位教练");

        //评分
        gradeTextView.setText(merchant.getAvgGrade()+"");
        timeGradeRb.setRating(merchant.getItem1());
        placeGradeRb.setRating(merchant.getItem2());
        serviceGradeRb.setRating(merchant.getItem3());
        timeGradeTv.setText(merchant.getItem1() + "分");
        placeGradeTv.setText(merchant.getItem2() + "分");
        serviceGradeTv.setText(merchant.getItem3() + "分");

        //暂时只有单个课程价格
        Course course = new Course();
        course.setPrice(merchant.getOurPrice());
        course.setSubject("自选教学时间");
        course.setType("普通班");
        course.setMerchantId(merchant.getSid());
        course.setMerchantName(merchant.getSname());
        courses.add(course);
        courseAdapter.notifyDataSetChanged();
        coachHorizontalAdapter.notifyDataSetChanged();

        //loading
        loading.setVisibility(View.GONE);
    }

    private void initEvent() {
        btnConsult.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        moreCoachBtn.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        merchantIntroTv.setOnClickListener(this);
        navigateLl.setOnClickListener(this);
        evaluationLl.setOnClickListener(this);
        moreEvaluationBtn.setOnClickListener(this);
    }

    private void getData() {
        merchantId = getIntent().getExtras().getInt("merchantId");
        getMerchantDetailData();
    }

    private void getMerchantDetailData() {
        Callback<Merchant> callback = new Callback<Merchant>() {
            @Override
            public void success(Merchant remoteMerchant, Response response) {
                merchant = remoteMerchant;
                getCoachListData();
                getEvaluationListData();
            }

            @Override
            public void failure(RetrofitError error) {
                RequestErrorHandler requestErrorHandler = new RequestErrorHandler(MerchantInfoActivity.this);
                try {
                    requestErrorHandler.handError(error);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetMerchantDetailRequester getMerchantDetailRequester = new GetMerchantDetailRequester(callback,merchantId);
        getMerchantDetailRequester.request();
    }
    private void getCoachListData() {
        Callback<List<Coach>> callback = new Callback<List<Coach>>() {
            @Override
            public void success(List<Coach> coachList, Response response) {
                coaches.addAll(coachList);
                refreshView();
            }

            @Override
            public void failure(RetrofitError error) {
                RequestErrorHandler requestErrorHandler = new RequestErrorHandler(MerchantInfoActivity.this);
                try {
                    requestErrorHandler.handError(error);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Map<String,Object> param = new HashMap<>();
        param.put("merchantId", merchantId);

        GetCoachListRequester getCoachListRequester = new GetCoachListRequester(callback,param);
        getCoachListRequester.request();
    }

    private void getEvaluationListData() {
        Callback<List<Evaluation>> callback = new Callback<List<Evaluation>>() {
            @Override
            public void success(List<Evaluation> evaluations, Response response) {
                moreEvaluationBtn.setText("查看全部"+evaluations.size()+"条评论");
                evaluationMerchantTtv.setText(evaluations.size()+"人评价");
                int showSize = 0;
                if (evaluations.size() == 1){
                    showSize = 1;
                }else if (evaluations.size() >= 2){
                    showSize = 2;
                }
                for (int i = 0;i < showSize;i++) {
                    View v = LayoutInflater.from(MerchantInfoActivity.this).inflate(R.layout.item_evaluation_rv, null);
                    TextView nameTv = (TextView) v.findViewById(R.id.name_tv);
                    TextView pubTimeTv = (TextView) v.findViewById(R.id.pubTime_tv);
                    TextView contentTv = (TextView) v.findViewById(R.id.content_tv);
                    RatingBar gradeRb = (RatingBar) v.findViewById(R.id.grade_rb);

                    nameTv.setText(evaluations.get(i).getUser().getUserName());
                    pubTimeTv.setText(DateUtils.getStandardDate(evaluations.get(i).getCreateTime()));
                    contentTv.setText(evaluations.get(i).getJudgeWord());
                    gradeRb.setRating(evaluations.get(i).getAvgGrade());
                    evaluationPreLl.addView(v);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                RequestErrorHandler requestErrorHandler = new RequestErrorHandler(MerchantInfoActivity.this);
                try {
                    requestErrorHandler.handError(error);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Map<String,Object> param = new HashMap<>();
        param.put("merchantId",merchantId);
        param.put("timestamp",System.currentTimeMillis());
        GetEvaluationListRequester getEvaluationListRequester = new GetEvaluationListRequester(callback,param);
        getEvaluationListRequester.request();
    }

    private void initView() {
        coachRv = (RecyclerView) findViewById(R.id.coach_merchant_rv);
        FullyLinearLayoutManager coachLinearLayoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        coachHorizontalAdapter = new CoachHorizontalAdapter(coaches,this);
        coachRv.setLayoutManager(coachLinearLayoutManager);
        coachRv.setAdapter(coachHorizontalAdapter);

        courseRv = (RecyclerView) findViewById(R.id.price_merchant_rv);
        FullyLinearLayoutManager courseLinearLayoutManager = new FullyLinearLayoutManager(this);
        courseAdapter = new CourseAdapter(courses,MerchantInfoActivity.this);
        courseRv.setLayoutManager(courseLinearLayoutManager);
        courseRv.setAdapter(courseAdapter);

        navigateLl = (LinearLayout) findViewById(R.id.navigate_rl);
        evaluationLl = (LinearLayout) findViewById(R.id.evaluation_merchant_ll);
        evaluationPreLl = (LinearLayout) findViewById(R.id.evaluation_merchant_pre_ll);
        loading = (LinearLayout) findViewById(R.id.loading);

        btnApply = (Button) findViewById(R.id.btn_apply);
        btnConsult = (Button) findViewById(R.id.btn_consult);
        moreCoachBtn = (Button) findViewById(R.id.allCoach_btn);
        moreEvaluationBtn = (Button) findViewById(R.id.allEvaluation_btn);

        tvName = (TextView) findViewById(R.id.tv_name);
        merchantIntroTv = (TextView) findViewById(R.id.merchantIntro_tv);
        tvCoachAmount = (TextView) findViewById(R.id.tv_coachAmount);
        studentAmountTextView = (TextView) findViewById(R.id.tvStudentAmount);
        gradeTextView = (TextView) findViewById(R.id.grade_merchant_tv);
        addressTv = (TextView) findViewById(R.id.address_tv);
        evaluationMerchantTtv = (TextView) findViewById(R.id.evaluation_merchant_tv);

        timeGradeRb = (RatingBar) findViewById(R.id.time_grade_rb);
        placeGradeRb = (RatingBar) findViewById(R.id.place_grade_rb);
        serviceGradeRb = (RatingBar) findViewById(R.id.service_grade_rb);

        timeGradeTv = (TextView) findViewById(R.id.time_grade_tv);
        placeGradeTv = (TextView) findViewById(R.id.place_grade_tv);
        serviceGradeTv = (TextView) findViewById(R.id.service_grade_tv);

        btnMap = (ImageButton) findViewById(R.id.btn_map);
    }

    @Override
    public void onClick(View v) {
        Bundle merchantBundle = new Bundle();
        merchantBundle.putInt("merchantId", merchant.getSid());
        merchantBundle.putSerializable("merchant",merchant);
        switch (v.getId()) {
            case R.id.btn_apply:
                startActivity(ResultCoachActivity.class, merchantBundle);
                break;
            case R.id.btn_consult:
                startActivity(ConsultActivity.class);
                break;
            case R.id.navigate_rl:
                Bundle bundle = new Bundle();
                bundle.putString("lat", merchant.getLatitude());
                bundle.putString("lng", merchant.getLongitude());
                startActivity(MerchantMapActivity.class, bundle);
                break;
            case R.id.merchantIntro_tv:
                Bundle introBundle = new Bundle();
                introBundle.putString("intro",merchant.getSintroduction());
                startActivity(ResultMerchantIntroActivity.class,introBundle);
                break;
            case R.id.allCoach_btn:
                startActivity(ResultCoachActivity.class,merchantBundle);
                break;
            case R.id.allEvaluation_btn:
                startActivity(EvaluationActivity.class,merchantBundle);
                break;
            case R.id.evaluation_merchant_ll:
                startActivity(EvaluationActivity.class,merchantBundle);
                break;
            default:
                break;
        }
    }
}
