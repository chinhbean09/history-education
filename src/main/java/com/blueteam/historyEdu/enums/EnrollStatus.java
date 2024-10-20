package com.blueteam.historyEdu.enums;

public enum EnrollStatus {
    SUCCESS,              // Đăng ký thành công
    USER_NOT_FOUND,       // Người dùng không tồn tại
    COURSE_NOT_FOUND,     // Khóa học không tồn tại
    UNPAID,               // Người dùng chưa thanh toán
    ALREADY_ENROLLED,     // Người dùng đã đăng ký khóa học
    ERROR                 // Lỗi trong quá trình xử lý
}
