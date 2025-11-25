# Todo App với Option Menu và Context Menu

Ứng dụng Todo đơn giản với các tính năng:

## Option Menu (Menu trên thanh công cụ)
- **New**: Thêm todo mới
- **Delete**: Xóa các todo đã chọn
- **Select All**: Chọn tất cả các todo

## Context Menu (Menu khi long-press vào todo item)
- **Edit**: Chỉnh sửa nội dung todo
- **Delete**: Xóa todo đó

## Cấu trúc dự án
- `MainActivity.java`: Activity chính xử lý logic
- `TodoItem.java`: Model cho todo item
- `TodoAdapter.java`: Adapter cho ListView
- `main_menu.xml`: Option menu
- `context_menu.xml`: Context menu
- `activity_main.xml`: Layout chính
- `todo_item.xml`: Layout cho mỗi todo item

## Cách sử dụng
1. Nhấn nút "New" để thêm todo mới
2. Tick checkbox để chọn todo
3. Nhấn "Delete" để xóa các todo đã chọn
4. Long-press vào todo để hiện context menu (Edit/Delete)
5. Nhấn "Select All" để chọn tất cả
