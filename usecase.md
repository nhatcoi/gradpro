# ĐẶC TẢ CHI TIẾT CÁC USE CASE

Tài liệu này trình bày chi tiết đặc tả các Use Case theo phân nhóm người dùng và thứ tự nghiệp vụ của hệ thống Quản lý Đồ án Tốt nghiệp.

---

## 1. NHÓM USE CASE CHUNG (NGƯỜI DÙNG HỆ THỐNG)

### UC-01: Đăng nhập
| Số và tên UC | UC-01: Đăng nhập hệ thống |
| :--- | :--- |
| Người tạo UC | Nhóm 12 | Ngày tạo UC | 18/01/2026 |
| Mô tả | Cho phép người dùng xác thực danh tính để truy cập vào hệ thống. |
| Tác nhân chính | Tất cả người dùng |
| Luồng thông thường | 1. Hệ thống hiển thị màn hình đăng nhập.<br>2. Người dùng nhập Mã số/Email và Mật khẩu.<br>3. Hệ thống kiểm tra thông tin và quyền hạn.<br>4. Điều hướng về Dashboard tương ứng. |

### UC-02: Đổi mật khẩu
| Số và tên UC | UC-02: Đổi mật khẩu |
| :--- | :--- |
| Mô tả | Cho phép người dùng chủ động thay đổi mật khẩu để bảo mật tài khoản. |

### UC-03: Cập nhật thông tin cá nhân
| Số và tên UC | UC-03: Cập nhật thông tin cá nhân |
| :--- | :--- |
| Mô tả | Người dùng cập nhật các thông tin liên hệ cá nhân (Số điện thoại, Email liên kết). |

---

## 2. ADMIN (QUẢN TRỊ VIÊN)

### UC-6.1: Quản lý người dùng
| Số và tên UC | UC-6.1: Quản lý người dùng |
| :--- | :--- |
| Mô tả | Tạo, sửa, xóa và phân quyền cho giảng viên, sinh viên, cán bộ. |
| Tác nhân chính | Admin |
| Luồng thông thường | 1. Admin vào danh sách người dùng.<br>2. Thêm mới tài khoản hoặc sửa thông tin hiện có.<br>3. Gán vai trò (Roles) cho tài khoản.<br>4. Hệ thống lưu vào CSDL. |

---

## 3. PHÒNG ĐÀO TẠO

### UC-2.1: Mở đợt đăng ký
| Số và tên UC | UC-2.1: Mở đợt đăng ký |
| :--- | :--- |
| Mô tả | Thiết lập thời gian bắt đầu và kết thúc cho đợt đăng ký đề tài của học kỳ. |

### UC-2.2: Phân công Hội đồng (Hội đồng bảo vệ)
| Số và tên UC | UC-2.2: Phân công HĐ |
| :--- | :--- |
| Mô tả | Phân chia danh sách các đồ án vào các hội đồng bảo vệ chuyên môn. |

### UC-2.3: Xếp lịch
| Số và tên UC | UC-2.3: Xếp lịch |
| :--- | :--- |
| Mô tả | Gán ngày, giờ và phòng bảo vệ cụ thể cho các hội đồng. |

### UC-2.3 (Tiếp): Thống kê (Bộ trưởng - Cấp trường)
| Số và tên UC | UC-2.3: Thống kê (Bộ trưởng) |
| :--- | :--- |
| Mô tả | Xem báo cáo tổng hợp tình hình đồ án trên quy mô toàn trường. |

### UC-2.4: Thống kê (Cấp trường)
| Số và tên UC | UC-2.4: Thống kê (Cấp trường) |
| :--- | :--- |
| Mô tả | Theo dõi các chỉ số về tỷ lệ sinh viên làm đồ án, giảng viên hướng dẫn. |

### UC-2.5: Xuất báo cáo (Cấp trường)
| Số và tên UC | UC-2.5: Xuất báo cáo |
| :--- | :--- |
| Mô tả | Trích xuất dữ liệu ra tệp Excel/PDF phục vụ lưu trữ hành chính. |

---

## 4. TRƯỞNG BỘ MÔN (BM) / KHOA

### UC-3.1: Phê duyệt đề tài
| Số và tên UC | UC-3.1: Phê duyệt đề tài |
| :--- | :--- |
| Mô tả | Xem xét tính khả thi và duyệt đề tài mở cho sinh viên đăng ký. |

### UC-3.2: Phân công GVHD
| Số và tên UC | UC-3.2: Phân công GVHD |
| :--- | :--- |
| Mô tả | Chỉ định giảng viên hướng dẫn cho các trường hợp sinh viên tự đề xuất hoặc chưa có người hướng dẫn. |

### UC-3.3: Tạo Hội đồng
| Số và tên UC | UC-3.3: Tạo hội đồng |
| :--- | :--- |
| Mô tả | Thiết lập cơ cấu thành viên hội đồng (Chủ tịch, Thư ký, Phản biện). |

### UC-3.5: Thống kê (Cấp Khoa)
| Số và tên UC | UC-3.5: Thống kê (Cấp Khoa) |
| :--- | :--- |
| Mô tả | Theo dõi tiến độ hoàn thành đồ án của sinh viên trong khoa. |

### UC-3.6: Xuất báo cáo (Cấp Khoa)
| Số và tên UC | UC-3.6: Xuất báo cáo |
| :--- | :--- |
| Mô tả | Xuất danh sách điểm và biên bản bảo vệ của khoa. |

---

## 5. GIẢNG VIÊN

### UC-4.1: Đăng ký đề tài mở
| Số và tên UC | UC-4.1: Đăng ký đề tài mở |
| :--- | :--- |
| Mô tả | Giảng viên đề xuất danh sách các đề tài mình có thể hướng dẫn. |

### UC-4.2: Tìm kiếm đề tài
| Số và tên UC | UC-4.2: Tìm kiếm đề tài |
| :--- | :--- |
| Mô tả | Tra cứu lịch sử đề tài hoặc các đề tài đang có trong hệ thống. |

### UC-4.3: Chọn sinh viên
| Số và tên UC | UC-4.3: Chọn sinh viên |
| :--- | :--- |
| Mô tả | Phê duyệt yêu cầu hướng dẫn của sinh viên đăng ký vào đề tài của mình. |

### UC-4.4: Thiết lập mốc (Deadline/Giai đoạn)
| Số và tên UC | UC-4.4: Thiết lập mốc thời gian |
| :--- | :--- |
| Mô tả | Chia nhỏ quá trình làm đồ án thành các giai đoạn để sinh viên nộp báo cáo. |

### UC-4.5: Đánh giá tiến độ
| Số và tên UC | UC-4.5: Đánh giá tiến độ |
| :--- | :--- |
| Mô tả | Nhận xét và đánh giá các báo cáo định kỳ của sinh viên. |

### UC-4.7: Chấm HĐ (Điểm hội đồng)
| Số và tên UC | UC-4.7: Chấm điểm hội đồng |
| :--- | :--- |
| Mô tả | Thành viên hội đồng nhập điểm cho sinh viên tại buổi bảo vệ. |

---

## 6. SINH VIÊN

### UC-5.1: Đề xuất đề tài (Nếu được phép)
| Số và tên UC | UC-5.1: Đề xuất đề tài |
| :--- | :--- |
| Mô tả | Sinh viên tự đề xuất ý tưởng đồ án và tìm giảng viên đồng ý hướng dẫn. |

### UC-5.2: Tìm kiếm đề tài
| Số và tên UC | UC-5.2: Tìm kiếm đề tài |
| :--- | :--- |
| Mô tả | Tra cứu kho đề tài đã được duyệt để chọn nguyện vọng. |

### UC-5.3: Đăng ký đề tài (Chọn đề tài có sẵn)
| Số và tên UC | UC-5.3: Đăng ký đề tài |
| :--- | :--- |
| Mô tả | Thực hiện đăng ký nguyện vọng vào các đề tài giảng viên đã mở. |

### UC-5.4: Cập nhật tiến độ
| Số và tên UC | UC-5.4: Cập nhật tiến độ |
| :--- | :--- |
| Mô tả | Viết báo cáo ngắn và nộp sản phẩm giai đoạn cho GVHD. |

### UC-5.5: Nộp báo cáo (Các giai đoạn)
| Số và tên UC | UC-5.5: Nộp báo cáo giai đoạn |
| :--- | :--- |
| Mô tả | Tải lên các tệp tài liệu chứng minh quá trình thực hiện. |

### UC-5.6: Nộp BC cuối (Khóa luận hoàn thiện)
| Số và tên UC | UC-5.6: Nộp báo cáo cuối kỳ |
| :--- | :--- |
| Mô tả | Nộp bản báo cáo hoàn thiện để đủ điều kiện xét bảo vệ và lưu trữ. |

### UC-5.7: Xem điểm
| Số và tên UC | UC-5.7: Xem điểm |
| :--- | :--- |
| Mô tả | Tra cứu điểm hướng dẫn và điểm hội đồng sau khi kết thúc đợt bảo vệ. |
