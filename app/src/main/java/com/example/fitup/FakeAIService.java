package com.example.fitup;

import java.util.Locale;

public class FakeAIService {
    private static boolean needsConsultation = false;
    public static String getReply(String userMsg) {
        userMsg = userMsg.toLowerCase(Locale.ROOT);

        // 💪 NGỰC (Chest)
        if (
                userMsg.equals("ok") ||
                        userMsg.equals("oke") ||
                        userMsg.equals("okay") ||
                        userMsg.equals("okela") ||

                        userMsg.equals("sure") ||
                        userMsg.equals("yes") ||
                        userMsg.equals("yeah") ||
                        userMsg.equals("yep") ||

                        userMsg.equals("co") ||
                        userMsg.equals("có") ||
                        userMsg.equals("uk") ||
                        userMsg.equals("ukm") ||
                        userMsg.equals("uh") ||
                        userMsg.equals("uhm") ||

                        userMsg.contains(" ok ") ||
                        userMsg.startsWith("ok ") ||
                        userMsg.endsWith(" ok") ||

                        userMsg.contains(" yes ") ||
                        userMsg.startsWith("yes ") ||
                        userMsg.endsWith(" yes")
        ) {
            return "🔥 Oke luôn bạn hiền, nhập nhóm cơ bạn muốn cải thiện nhé hoặc mục tiêu của bạn nhe! 💪";
        }
        if (userMsg.contains("ngực") || userMsg.contains("chest")) {
            return "💪 Nhóm cơ ngực nên tập: Push-up, Bench Press, Dumbbell Fly, Cable Crossover. Tập 3–4 buổi/tuần, mỗi bài 3-4 sets x 8-12 reps.";
        }
        if (userMsg.contains("push up") || userMsg.contains("hít đất")) {
            return "💪 Push-up là bài tập cơ bản tuyệt vời! Giữ thân thẳng, tay rộng bằng vai, hạ thấp đến gần sàn rồi đẩy lên. Bắt đầu với 3 sets x 10-15 reps.";
        }
        if (userMsg.contains("bench press") || userMsg.contains("đẩy tạ")) {
            return "🏋️ Bench Press phát triển cơ ngực tối đa. Nằm ngửa, hạ tạ về ngực, đẩy lên thẳng. Khởi động kỹ, tập 4 sets x 6-10 reps với trọng lượng vừa phải.";
        }

        // 💪 TAY TRƯỚC (Biceps)
        if (userMsg.contains("tay trước") || userMsg.contains("biceps") || userMsg.contains("bắp tay")) {
            return "💪 Biceps nên tập: Barbell Curl, Dumbbell Curl, Hammer Curl, Concentration Curl. Tập 2-3 buổi/tuần, mỗi bài 3 sets x 10-12 reps.";
        }
        if (userMsg.contains("barbell curl")) {
            return "💪 Barbell Curl: Đứng thẳng, nâng thanh tạ lên bằng cách gập khuỷu tay, giữ khuỷu tay cố định. 3-4 sets x 8-12 reps.";
        }
        if (userMsg.contains("hammer curl")) {
            return "💪 Hammer Curl tập cả biceps và cơ cẳng tay. Cầm tạ theo kiểu búa, nâng lên không xoay cổ tay. 3 sets x 10-12 reps.";
        }

        // 💪 TAY SAU (Triceps)
        if (userMsg.contains("tay sau") || userMsg.contains("triceps")) {
            return "💪 Triceps chiếm 2/3 cánh tay! Tập: Triceps Dip, Overhead Extension, Rope Pushdown. 3 sets x 10-15 reps.";
        }
        if (userMsg.contains("triceps dip")) {
            return "💪 Triceps Dip: Dùng ghế hoặc xà kép, hạ người xuống bằng tay sau rồi đẩy lên. Bài tập siêu hiệu quả! 3-4 sets x 8-12 reps.";
        }

        // 🦵 CHÂN (Legs)
        if (userMsg.contains("chân") || userMsg.contains("leg") || userMsg.contains("đùi")) {
            return "🦵 Chân nên tập: Squat, Leg Press, Lunges, Leg Curl, Calf Raises. Đừng bỏ ngày chân! Tập 2-3 buổi/tuần.";
        }
        if (userMsg.contains("squat") || userMsg.contains("gánh tạ")) {
            return "🦵 Squat là vua của các bài tập chân! Đứng chân rộng vai, hạ xuống như ngồi ghế, đẩy gót chân lên. 4 sets x 8-12 reps.";
        }
        if (userMsg.contains("leg press")) {
            return "🦵 Leg Press an toàn cho đầu gối, phát triển toàn bộ đùi. Đặt chân rộng vai trên bệ, đẩy mạnh. 3-4 sets x 10-15 reps.";
        }
        if (userMsg.contains("lunges") || userMsg.contains("chân chống")) {
            return "🦵 Lunges tập đùi và mông cực tốt. Bước dài về phía trước, hạ đầu gối xuống 90°, đẩy lên. 3 sets x 12-15 reps mỗi chân.";
        }
        if (userMsg.contains("calf") || userMsg.contains("bắp chân")) {
            return "🦵 Bắp chân cần Calf Raises: Đứng nhón gót lên cao, giữ 1 giây rồi hạ xuống. Tập 4 sets x 15-20 reps.";
        }

        // 🏋️ VAI (Shoulders)
        if (userMsg.contains("vai") || userMsg.contains("shoulder") || userMsg.contains("delta")) {
            return "🏋️ Vai nên tập: Overhead Press, Lateral Raise, Front Raise, Face Pull. Vai khỏe giúp dáng đẹp! 3 sets x 10-12 reps.";
        }
        if (userMsg.contains("overhead press")) {
            return "🏋️ Overhead Press: Đứng hoặc ngồi, đẩy tạ lên trên đầu thẳng đứng. Bài tập toàn diện cho vai. 4 sets x 6-10 reps.";
        }
        if (userMsg.contains("lateral raise")) {
            return "🏋️ Lateral Raise tập vai giữa: Nâng tạ ra hai bên đến ngang vai, hạ chậm. 3 sets x 12-15 reps.";
        }

        // 🔥 LƯNG (Back)
        if (userMsg.contains("lưng") || userMsg.contains("back")) {
            return "🔥 Lưng nên tập: Pull-up, Deadlift, Barbell Row, Lat Pulldown. Lưng rộng tạo dáng chữ V! 3-4 sets x 8-12 reps.";
        }
        if (userMsg.contains("pull up") || userMsg.contains("xà đơn")) {
            return "🔥 Pull-up là bài tập lưng tốt nhất! Treo xà, kéo người lên đến cằm qua xà. Bắt đầu với 3 sets x 5-10 reps.";
        }
        if (userMsg.contains("deadlift") || userMsg.contains("nâng tạ đất")) {
            return "🔥 Deadlift tập toàn thân, đặc biệt lưng dưới! Giữ lưng thẳng, nâng tạ lên bằng hông và chân. 3-4 sets x 5-8 reps.";
        }
        if (userMsg.contains("barbell row")) {
            return "🔥 Barbell Row: Cúi người 45°, kéo tạ về bụng, siết cơ lưng. Tuyệt vời cho lưng giữa! 3-4 sets x 8-12 reps.";
        }

        // 🍑 MÔNG (Glutes)
        if (userMsg.contains("mông") || userMsg.contains("glutes") || userMsg.contains("hip")) {
            return "🍑 Mông săn chắc: Hip Thrust, Glute Bridge, Bulgarian Split Squat, Cable Kickback. 3-4 sets x 12-15 reps.";
        }
        if (userMsg.contains("hip thrust")) {
            return "🍑 Hip Thrust: Tựa lưng vào ghế, đặt tạ trên hông, đẩy hông lên cao. Bài tập mông số 1! 4 sets x 10-15 reps.";
        }
        if (userMsg.contains("glute bridge")) {
            return "🍑 Glute Bridge: Nằm ngửa, gập đầu gối, nâng hông lên cao, siết mông. Dễ tập tại nhà! 3 sets x 15-20 reps.";
        }

        // 🧘 BỤNG (Abs/Core)
        if (userMsg.contains("bụng") || userMsg.contains("abs") || userMsg.contains("six pack") || userMsg.contains("core")) {
            return "🧘 Bụng 6 múi: Plank, Crunches, Leg Raises, Russian Twist, Mountain Climbers. Tập 4-5 buổi/tuần, kết hợp giảm mỡ!";
        }
        if (userMsg.contains("plank")) {
            return "🧘 Plank tập toàn bộ core! Chống tay, giữ thân thẳng như tấm ván. Bắt đầu 3 sets x 30-60 giây.";
        }
        if (userMsg.contains("crunches") || userMsg.contains("gập bụng")) {
            return "🧘 Crunches: Nằm ngửa, gập đầu gối, nâng vai lên khỏi sàn, siết bụng. 3 sets x 15-25 reps.";
        }
        if (userMsg.contains("leg raises")) {
            return "🧘 Leg Raises tập bụng dưới: Nằm ngửa, nâng chân thẳng lên 90°, hạ chậm. 3 sets x 12-15 reps.";
        }
        if (userMsg.contains("russian twist")) {
            return "🧘 Russian Twist tập bụng chéo: Ngồi nghiêng về sau, xoay thân hai bên. 3 sets x 20-30 reps mỗi bên.";
        }

        // 🥗 DINH DƯỠNG TỔNG QUÁT
        if (userMsg.contains("ăn") || userMsg.contains("dinh dưỡng") || userMsg.contains("nutrition")) {
            return "🥗 Dinh dưỡng là 70% thành công! Ăn đủ đạm (1.6-2.2g/kg), tinh bột tốt (gạo lứt, khoai), rau xanh và trái cây. Uống 2-3L nước/ngày.";
        }
        if (userMsg.contains("protein") || userMsg.contains("đạm")) {
            return "🥩 Protein xây dựng cơ: Ăn thịt gà, cá, trứng, đậu hũ, sữa. Cần 1.6-2.2g/kg cơ thể. Sau tập nên ăn protein trong 2h.";
        }
        if (userMsg.contains("carb") || userMsg.contains("tinh bột") || userMsg.contains("carbonhydrate")) {
            return "🍚 Carbs cung cấp năng lượng: Ăn gạo lứt, khoai lang, yến mạch, chuối. Tránh đường tinh luyện và bánh ngọt.";
        }
        if (userMsg.contains("chất béo") || userMsg.contains("fat")) {
            return "🥑 Chất béo tốt rất quan trọng: Dầu olive, bơ, hạt điều, cá hồi. Tránh dầu chiên nhiều lần và thức ăn nhanh.";
        }
        if (userMsg.contains("vitamin")) {
            return "🍊 Vitamin quan trọng: Vitamin D (nắng, cá), C (cam, ớt), B (ngũ cốc), E (hạt). Ăn đa dạng màu sắc rau củ quả!";
        }
        if (userMsg.contains("khoáng chất") || userMsg.contains("mineral")) {
            return "⚡ Khoáng chất cần thiết: Canxi (sữa, đậu), Sắt (thịt đỏ, rau bina), Magie (hạt, chuối), Kẽm (hải sản).";
        }

        // 🍗 THỰC PHẨM CỤ THỂ
        if (userMsg.contains("trứng") || userMsg.contains("egg")) {
            return "🥚 Trứng là siêu thực phẩm! Giàu protein, vitamin và khoáng chất. Ăn 2-3 quả/ngày không vấn đề. Luộc hoặc ốp la ít dầu.";
        }
        if (userMsg.contains("gà") || userMsg.contains("chicken")) {
            return "🍗 Ức gà là nguồn protein gầu tuyệt vời! 100g có ~30g protein. Nướng, luộc hoặc hấp là tốt nhất.";
        }
        if (userMsg.contains("cá") || userMsg.contains("fish")) {
            return "🐟 Cá giàu protein và Omega-3 tốt cho tim mạch. Ưu tiên cá hồi, cá thu, cá ngừ. Ăn 2-3 lần/tuần.";
        }
        if (userMsg.contains("thịt bò") || userMsg.contains("beef")) {
            return "🥩 Thịt bò giàu protein, sắt và B12. Chọn phần nạc, nướng hoặc hầm. Ăn 2-3 lần/tuần là đủ.";
        }
        if (userMsg.contains("sữa") || userMsg.contains("milk")) {
            return "🥛 Sữa cung cấp protein và canxi. Chọn sữa tách béo nếu giảm cân, sữa nguyên kem nếu tăng cân. Uống 1-2 ly/ngày.";
        }
        if (userMsg.contains("yến mạch") || userMsg.contains("oats")) {
            return "🥣 Yến mạch giàu chất xơ và carbs tốt, no lâu. Ăn sáng với chuối, mật ong là hoàn hảo! 50-80g/bữa.";
        }
        if (userMsg.contains("khoai") || userMsg.contains("sweet potato")) {
            return "🍠 Khoai lang nguồn carbs tuyệt vời, giàu vitamin A. Hấp hoặc nướng, ăn trước tập 1-2h.";
        }
        if (userMsg.contains("chuối") || userMsg.contains("banana")) {
            return "🍌 Chuối cung cấp năng lượng nhanh, giàu kali chống chuột rút. Ăn trước tập 30 phút hoặc sau tập.";
        }
        if (userMsg.contains("bơ") || userMsg.contains("avocado")) {
            return "🥑 Bơ giàu chất béo tốt, vitamin E. Tốt cho da và tim mạch. Ăn nửa quả/ngày, kết hợp salad hoặc bánh mì.";
        }
        if (userMsg.contains("hạt") || userMsg.contains("nuts")) {
            return "🌰 Hạt điều, hạnh nhân, óc chó giàu chất béo tốt và protein. Snack hoàn hảo! Ăn 1 nắm/ngày.";
        }
        if (userMsg.contains("đậu") || userMsg.contains("beans")) {
            return "🫘 Đậu giàu protein thực vật, chất xơ. Đậu đen, đậu đỏ, đậu hủ đều tốt. Người ăn chay nên ăn nhiều!";
        }
        if (userMsg.contains("rau xanh") || userMsg.contains("vegetables")) {
            return "🥬 Rau xanh giàu vitamin, khoáng chất, chất xơ. Ăn cải bó xôi, súp lơ, bông cải xanh mỗi bữa. Ít calo, no lâu!";
        }
        if (userMsg.contains("trái cây") || userMsg.contains("fruit")) {
            return "🍎 Trái cây cung cấp vitamin và chất xơ. Ăn táo, cam, dâu, nho. Nhưng đừng ăn quá nhiều vì có đường!";
        }

        // 💊 BỔ SUNG (Supplements)
        if (userMsg.contains("whey") || userMsg.contains("protein powder")) {
            return "💊 Whey protein tiện lợi sau tập. 1 scoop (25-30g protein) với nước hoặc sữa. Uống trong 30-60 phút sau tập.";
        }
        if (userMsg.contains("creatine")) {
            return "💊 Creatine tăng sức mạnh và sức bền. Uống 5g/ngày, bất kỳ lúc nào. An toàn và được nghiên cứu kỹ.";
        }
        if (userMsg.contains("bcaa")) {
            return "💊 BCAA giúp phục hồi cơ. Uống trong lúc tập hoặc sau tập. Nhưng nếu ăn đủ protein thì không cần thiết lắm.";
        }
        if (userMsg.contains("pre-workout")) {
            return "💊 Pre-workout tăng năng lượng và tập trung. Uống trước tập 20-30 phút. Chú ý lượng caffeine, đừng uống tối!";
        }
        if (userMsg.contains("vitamin d")) {
            return "☀️ Vitamin D quan trọng cho xương và miễn dịch. Ra nắng 15-20 phút/ngày hoặc uống viên 1000-2000 IU.";
        }
        if (userMsg.contains("omega 3") || userMsg.contains("fish oil")) {
            return "🐟 Omega-3 tốt cho tim mạch và chống viêm. Ăn cá béo 2-3 lần/tuần hoặc uống viên dầu cá 1-2g/ngày.";
        }

        // 💧 NƯỚC & HYDRATION
        if (userMsg.contains("nước") || userMsg.contains("water") || userMsg.contains("hydration")) {
            return "💧 Uống đủ nước rất quan trọng! 2-3L/ngày, tăng lên khi tập. Nước giúp phục hồi, tiêu hóa và da đẹp.";
        }
        if (userMsg.contains("nước uống khi tập")) {
            return "💧 Uống nước trước, trong và sau tập! 500ml trước tập, 200-300ml mỗi 15-20 phút khi tập, 500ml sau tập.";
        }

        // 😴 NGHỈ NGƠI & PHỤC HỒI
        if (userMsg.contains("ngủ") || userMsg.contains("sleep")) {
            return "😴 Ngủ 7-8 tiếng/ngày giúp cơ phục hồi và phát triển. Ngủ sớm trước 11h tối, tránh điện thoại trước khi ngủ.";
        }
        if (userMsg.contains("phục hồi") || userMsg.contains("recovery")) {
            return "🔄 Phục hồi quan trọng như tập luyện! Ngủ đủ, ăn đủ đạm, massage, foam rolling. Nghỉ 1-2 ngày/tuần.";
        }
        if (userMsg.contains("massage") || userMsg.contains("xoa bóp")) {
            return "💆 Massage giúp giảm đau cơ và phục hồi nhanh. Tự xoa bóp hoặc dùng foam roller sau tập 10-15 phút.";
        }
        if (userMsg.contains("foam roller")) {
            return "🔵 Foam roller giảm căng cơ và tăng linh hoạt. Lăn chậm trên từng nhóm cơ 1-2 phút/ngày.";
        }
        if (userMsg.contains("stretching") || userMsg.contains("giãn cơ")) {
            return "🧘 Giãn cơ tăng độ linh hoạt và giảm chấn thương. Giãn tĩnh sau tập 10-15 phút, giữ mỗi tư thế 20-30 giây.";
        }
        if (userMsg.contains("yoga")) {
            return "🧘 Yoga tuyệt vời cho linh hoạt, thăng bằng và tinh thần. Tập yoga 1-2 buổi/tuần bổ sung cho gym.";
        }
        if (userMsg.contains("sauna") || userMsg.contains("xông hơi")) {
            return "🔥 Sauna giúp thư giãn, tăng tuần hoàn máu. Xông 10-20 phút sau tập, nhớ uống nhiều nước!";
        }
        if (userMsg.contains("ice bath") || userMsg.contains("tắm nước lạnh")) {
            return "🧊 Tắm nước lạnh giảm viêm và phục hồi nhanh. 10-15 phút trong nước 10-15°C sau tập nặng.";
        }

        // 🔥 CARDIO
        if (userMsg.contains("cardio") || userMsg.contains("tim mạch")) {
            return "🔥 Cardio tốt cho tim mạch và giảm mỡ: Chạy bộ, đạp xe, bơi lội, nhảy dây. Tập 3-5 buổi/tuần, 20-45 phút.";
        }
        if (userMsg.contains("chạy bộ") || userMsg.contains("running") || userMsg.contains("jogging")) {
            return "🏃 Chạy bộ đốt calo hiệu quả! Bắt đầu 20-30 phút, tăng dần. Giày tốt quan trọng để bảo vệ đầu gối.";
        }
        if (userMsg.contains("đạp xe") || userMsg.contains("cycling")) {
            return "🚴 Đạp xe dễ cho khớp, đốt calo tốt. Đạp 30-60 phút, cường độ vừa đến cao. Ngoài trời hoặc xe đạp tại chỗ.";
        }
        if (userMsg.contains("bơi") || userMsg.contains("swimming")) {
            return "🏊 Bơi lội tập toàn thân, dễ cho khớp. Bơi 30-45 phút, 2-3 buổi/tuần. Tuyệt vời cho phục hồi chấn thương!";
        }
        if (userMsg.contains("nhảy dây") || userMsg.contains("jump rope")) {
            return "🪢 Nhảy dây đốt calo cực nhanh! 10-20 phút = 30 phút chạy. Bắt đầu 5 phút, tăng dần. Tốt cho bắp chân!";
        }
        if (userMsg.contains("hiit")) {
            return "⚡ HIIT (High Intensity Interval Training) đốt mỡ cực mạnh! 20-30 phút: nhanh 30s, nghỉ 30s. 3-4 buổi/tuần.";
        }
        if (userMsg.contains("walking") || userMsg.contains("đi bộ")) {
            return "🚶 Đi bộ nhẹ nhàng nhưng hiệu quả! 10,000 bước/ngày, hoặc 30-60 phút. Tốt cho sức khỏe tổng quát và giảm mỡ.";
        }

        // 🏋️ LỊCH TẬP
        if (userMsg.contains("lịch tập") || userMsg.contains("workout schedule") || userMsg.contains("split")) {
            return "📅 Lịch tập phổ biến:\n• Push/Pull/Legs (3 ngày)\n• Upper/Lower (4 ngày)\n• Full Body (3 ngày)\n• Bro Split (5-6 ngày: Ngực, Lưng, Vai, Tay, Chân)";
        }
        if (userMsg.contains("push pull legs") || userMsg.contains("ppl")) {
            return "📅 Push/Pull/Legs:\n• Push: Ngực, Vai, Tay sau\n• Pull: Lưng, Tay trước\n• Legs: Chân, Mông\nLặp 2 lần/tuần = 6 ngày tập.";
        }
        if (userMsg.contains("upper lower")) {
            return "📅 Upper/Lower:\n• Upper: Toàn thân trên\n• Lower: Toàn thân dưới\nTập 4 ngày/tuần, xen kẽ Upper-Lower.";
        }
        if (userMsg.contains("full body")) {
            return "📅 Full Body: Tập toàn thân mỗi buổi, 3 ngày/tuần. Phù hợp người mới hoặc bận rộn. Tập chính 1 bài/nhóm cơ.";
        }
        if (userMsg.contains("bro split")) {
            return "📅 Bro Split: 1 nhóm cơ/ngày, 5-6 ngày/tuần:\n• Thứ 2: Ngực\n• Thứ 3: Lưng\n• Thứ 4: Vai\n• Thứ 5: Tay\n• Thứ 6: Chân\n• Thứ 7: Nghỉ/Bụng";
        }

        // 💯 MỤC TIÊU & TRAINING
        if (userMsg.contains("tăng cơ") || userMsg.contains("build muscle") || userMsg.contains("hypertrophy")) {
            return "💪 Tăng cơ: Tập nặng 8-12 reps, 3-5 sets. Ăn thừa calo 300-500, đạm 2g/kg. Nghỉ đủ, tập 4-6 ngày/tuần.";
        }
        if (userMsg.contains("giảm mỡ") || userMsg.contains("lose fat") || userMsg.contains("cut")) {
            return "🔥 Giảm mỡ: Thiếu calo 300-500/ngày, tăng cardio. Tập tạ để giữ cơ, đạm cao 2-2.2g/kg. Kiên trì 8-12 tuần.";
        }
        if (userMsg.contains("tăng sức mạnh") || userMsg.contains("strength") || userMsg.contains("powerlifting")) {
            return "🏋️ Tăng sức mạnh: Tập nặng 3-6 reps, 4-6 sets. Tập 3 động tác chính: Squat, Bench, Deadlift. Nghỉ dài 3-5 phút.";
        }
        if (userMsg.contains("sức bền") || userMsg.contains("endurance")) {
            return "⏱️ Tăng sức bền: Tập nhẹ 15-20 reps, 3 sets. Giảm nghỉ ngơi 30-60s. Kết hợp cardio và circuit training.";
        }
        if (userMsg.contains("người mới") || userMsg.contains("beginner") || userMsg.contains("newbie")) {
            return "🌟 Người mới: Bắt đầu Full Body 3 ngày/tuần. Học kỹ thuật trước, tạ nhẹ 10-12 reps. 1-2 tháng đầu tập quen.";
        }
        if (userMsg.contains("nâng cao") || userMsg.contains("advanced")) {
            return "🔥 Người tập lâu năm: Tăng cường độ với Drop sets, Supersets, Rest-pause. Thay đổi lịch tập mỗi 8-12 tuần.";
        }

        // 🏆 KỸ THUẬT & FORM
        if (userMsg.contains("kỹ thuật") || userMsg.contains("form") || userMsg.contains("technique")) {
            return "✅ Kỹ thuật đúng quan trọng hơn trọng lượng! Động tác đầy đủ, kiểm soát, không xóc. Giảm chấn thương, hiệu quả sẽ tốt hơn nhé";
        }
        if (userMsg.contains("progressive overload")) {
            return "📈 Progressive Overload: Tăng dần trọng lượng, reps hoặc sets mỗi tuần để cơ phát triển. Cơ sở của mọi tiến bộ!";
        }
        if (userMsg.contains("tempo")) {
            return "⏱️ Tempo training: Kiểm soát tốc độ động tác. VD: 3-1-1 (3s hạ, 1s dừng, 1s nâng). Tăng thời gian căng cơ = tăng cơ.";
        }
        if (userMsg.contains("range of motion") || userMsg.contains("rom")) {
            return "📏 Range of Motion đầy đủ quan trọng! Động tác từ đầu đến cuối, không gian lận. Tăng hiệu quả và linh hoạt.";
        }
        if (userMsg.contains("mind muscle connection")) {
            return "🧠 Mind-Muscle Connection: Tập trung cảm nhận cơ đang làm việc. Làm chậm, siết cơ đúng. Hiệu quả hơn chỉ đẩy tạ!";
        }

        // 🎯 KỸ THUẬT NÂNG CAO
        if (userMsg.contains("drop set")) {
            return "💥 Drop Set: Tập đến kiệt sức, giảm tạ 20-30%, tiếp tục đến kiệt sức. Tăng cường độ cực mạnh! 1-2 lần/bài cuối.";
        }
        if (userMsg.contains("superset")) {
            return "⚡ Superset: Tập 2 bài liên tiếp không nghỉ. Tiết kiệm thời gian, tăng cường độ. VD: Bicep Curl + Tricep Extension.";
        }
        if (userMsg.contains("giant set")) {
            return "🔥 Giant Set: 3-4 bài liên tiếp không nghỉ cho cùng nhóm cơ. Cực khó, đốt cháy cơ! Chỉ người tập lâu năm.";
        }
        if (userMsg.contains("rest-pause")) {
            return "⏸️ Rest-Pause: Tập đến kiệt sức, nghỉ 15-20s, tiếp tục 2-3 reps. Lặp 2-3 lần. Tăng tổng reps hiệu quả!";
        }
        if (userMsg.contains("pyramid")) {
            return "🔺 Pyramid: Bắt đầu tạ nhẹ nhiều reps, tăng dần tạ giảm reps. Hoặc ngược lại. Tốt cho cả sức mạnh và sức bền.";
        }
        if (userMsg.contains("time under tension") || userMsg.contains("tut")) {
            return "⏱️ Time Under Tension: Tổng thời gian cơ căng trong 1 set. 40-70s tốt cho tăng cơ. Làm chậm động tác!";
        }
        // 🤕 CHẤN THƯƠNG & PHÒNG TRÁNH
        if (userMsg.contains("chấn thương") || userMsg.contains("injury")) {
            return "⚠️ Phòng chấn thương: Khởi động kỹ, kỹ thuật đúng, không ego lifting. Nghe cơ thể, nghỉ khi đau. Giãn cơ sau tập.";
        }
        if (userMsg.contains("khởi động") || userMsg.contains("warm up")) {
            return "🔥 Khởi động 5-10 phút trước tập: Cardio nhẹ + động tác khớp + bài tập tạ nhẹ. Giảm chấn thương, tăng hiệu suất!";
        }

        if (userMsg.contains("drop set")) {
            return "💥 Drop Set: Tập đến kiệt sức, giảm tạ 20-30%, tiếp tục đến kiệt sức. Tăng cường độ cực mạnh! 1-2 lần/bài cuối.";
        }
        if (userMsg.contains("superset")) {
            return "⚡ Superset: Tập 2 bài liên tiếp không nghỉ. Tiết kiệm thời gian, tăng cường độ. VD: Bicep Curl + Tricep Extension.";
        }
        if (userMsg.contains("giant set")) {
            return "🔥 Giant Set: 3-4 bài liên tiếp không nghỉ cho cùng nhóm cơ. Cực khó, đốt cháy cơ! Chỉ người tập lâu năm.";
        }
        if (userMsg.contains("rest-pause")) {
            return "⏸️ Rest-Pause: Tập đến kiệt sức, nghỉ 15-20s, tiếp tục 2-3 reps. Lặp 2-3 lần. Tăng tổng reps hiệu quả!";
        }
        if (userMsg.contains("pyramid")) {
            return "🔺 Pyramid: Bắt đầu tạ nhẹ nhiều reps, tăng dần tạ giảm reps. Hoặc ngược lại. Tốt cho cả sức mạnh và sức bền.";
        }
        if (userMsg.contains("time under tension") || userMsg.contains("tut")) {
            return "⏱️ Time Under Tension: Tổng thời gian cơ căng trong 1 set. 40-70s tốt cho tăng cơ. Làm chậm động tác!";
        }

        // 🤕 CHẤN THƯƠNG & PHÒNG TRÁNH
        if (userMsg.contains("chấn thương") || userMsg.contains("injury")) {
            return "⚠️ Phòng chấn thương: Khởi động kỹ, kỹ thuật đúng, không ego lifting. Nghe cơ thể, nghỉ khi đau. Giãn cơ sau tập.";
        }
        if (userMsg.contains("khởi động") || userMsg.contains("warm up")) {
            return "🔥 Khởi động 5-10 phút trước tập: Cardio nhẹ + động tác khớp + bài tập tạ nhẹ. Giảm chấn thương, tăng hiệu suất!";
        }
        if (userMsg.contains("đau lưng") || userMsg.contains("back pain")) {
            return "💔 Đau lưng: Kiểm tra kỹ thuật Deadlift, Squat. Tăng cường cơ core, giãn cơ thường xuyên. Nếu đau kéo dài, gặp bác sĩ!";
        }
        if (userMsg.contains("đau đầu gối") || userMsg.contains("knee pain")) {
            return "🦵 Đau đầu gối: Kiểm tra Squat form (đầu gối không vượt mũi chân). Tăng cường cơ đùi. Giảm tạ nếu cần, gặp bác sĩ nếu nặng.";
        }
        if (userMsg.contains("đau vai") || userMsg.contains("shoulder pain")) {
            return "💢 Đau vai: Tránh Bench Press sâu quá, kiểm tra form Overhead Press. Tập rotator cuff, giãn cơ vai thường xuyên.";
        }
        if (userMsg.contains("quá tải") || userMsg.contains("overtraining")) {
            return "😵 Quá tải (Overtraining): Mệt mỏi kéo dài, không tiến bộ, hay ốm. Nghỉ 3-5 ngày, ngủ nhiều, ăn đủ. Giảm khối lượng tập.";
        }

        // 📱 TRACKING & MOTIVATION
        if (userMsg.contains("tracking") || userMsg.contains("theo dõi")) {
            return "📊 Theo dõi tiến độ: Ghi nhật ký tập, cân nặng, ảnh hàng tuần. Giúp thấy tiến bộ, điều chỉnh kịp thời!";
        }
        if (userMsg.contains("động lực") || userMsg.contains("motivation")) {
            return "🔥 Giữ động lực: Đặt mục tiêu rõ ràng, tìm bạn tập, nghe nhạc, theo dõi tiến độ. Nhớ: Kỷ luật > Động lực!";
        }
        if (userMsg.contains("plateau") || userMsg.contains("đình trệ")) {
            return "📉 Đình trệ: Thay đổi bài tập, tăng cường độ, kiểm tra dinh dưỡng, nghỉ ngơi nhiều hơn. Thường xuyên sau 8-12 tuần.";
        }
        if (userMsg.contains("deload")) {
            return "🔄 Deload: Nghỉ giảm tải 1 tuần sau 6-8 tuần tập nặng. Giảm 40-50% volume/intensity. Phục hồi, tránh quá tải.";
        }

        // 🎯 MỤC TIÊU CỤ THỂ
        if (userMsg.contains("marathon")) {
            return "🏃 Chuẩn bị Marathon: Chạy 4-5 ngày/tuần, tăng km từ từ. Long run 1 lần/tuần. Giày tốt, dinh dưỡng đủ. Luyện 16-20 tuần.";
        }
        if (userMsg.contains("pull up 10 cái") || userMsg.contains("pull up goal")) {
            return "💪 Mục tiêu 10 Pull-ups: Tập Negative Pull-ups, Assisted Pull-ups, Lat Pulldown. Tập 3-4 buổi/tuần. Giảm cân nếu cần.";
        }
        if (userMsg.contains("six pack")) {
            return "🧘 Six pack = Giảm mỡ bụng (dưới 12-15% body fat) + Tập bụng. Ưu tiên dinh dưỡng, thâm hụt calo, cardio + tập bụng 3-5 buổi/tuần.";
        }

        // 🏃 CHẠY BỘ CHI TIẾT
        if (userMsg.contains("chạy 5km") || userMsg.contains("5k run")) {
            return "🏃 Chạy 5K cho người mới: Tuần 1-4: đi/chạy xen kẽ 20-30 phút. Tuần 5-8: chạy liên tục 25-35 phút. Mục tiêu: dưới 30 phút!";
        }
        if (userMsg.contains("chạy 10km") || userMsg.contains("10k run")) {
            return "🏃 Chạy 10K: Nền tảng 5K trước. Tăng 10% km/tuần, long run chậm 1 lần/tuần. Khoảng 8-12 tuần chuẩn bị.";
        }
        if (userMsg.contains("half marathon")) {
            return "🏃 Half Marathon (21km): Chạy được 10K trước. Luyện 12-16 tuần, tăng dần long run. Peak 18-20km, nghỉ giảm tải trước ngày thi.";
        }
        if (userMsg.contains("fartlek")) {
            return "⚡ Fartlek (tốc độ thay đổi): Chạy nhanh/chậm xen kẽ tự do, không cấu trúc. Vui, tăng sức bền và tốc độ. 20-40 phút.";
        }
        if (userMsg.contains("tempo run")) {
            return "⏱️ Tempo Run: Chạy ở 80-90% max effort, duy trì 20-40 phút. Tăng ngưỡng lactate, chạy nhanh lâu hơn. 1 lần/tuần.";
        }
        if (userMsg.contains("interval training chạy")) {
            return "🔥 Interval chạy: Chạy nhanh 1-5 phút, nghỉ bằng hoặc ít hơn. VD: 400m nhanh, jog 200m. 6-10 lần. Tăng VO2 max!";
        }

        // 🏋️ POWERLIFTING CHUYÊN SÂU
        if (userMsg.contains("squat form") || userMsg.contains("kỹ thuật squat")) {
            return "🦵 Squat form: Chân rộng vai, mũi chân hơi ra ngoài. Hít sâu, gánh tạ thấp. Hạ xuống như ngồi ghế, đầu gối theo mũi chân. Đẩy gót chân lên.";
        }
        if (userMsg.contains("bench press form") || userMsg.contains("kỹ thuật bench")) {
            return "💪 Bench Press form: Nằm ngửa, chân chặt sàn. Tạ rộng hơn vai, hạ về giữa ngực. Khuỷu tay 45°, đẩy thẳng lên. Siết mông, vai.";
        }
        if (userMsg.contains("deadlift form") || userMsg.contains("kỹ thuật deadlift")) {
            return "🔥 Deadlift form: Chân rộng hông, tạ sát ống chân. Lưng thẳng, vai trước tạ. Kéo bằng chân trước, rồi đứng thẳng. Hông và vai cùng lên.";
        }
        if (userMsg.contains("overhead press form")) {
            return "🏋️ Overhead Press form: Đứng thẳng, chân rộng vai. Tạ ngang vai, đẩy thẳng lên qua đầu. Siết mông, core. Không cong lưng!";
        }

        // 🍽️ MÓN ĂN VÀ CÔNG THỨC
        if (userMsg.contains("bữa sáng") || userMsg.contains("breakfast")) {
            return "🍳 Bữa sáng lý tưởng: Trứng (2-3 quả) + Yến mạch + Chuối + Sữa. Hoặc: Bánh mì nguyên cám + Bơ + Trứng. Đủ protein và carbs!";
        }
        if (userMsg.contains("bữa trưa") || userMsg.contains("lunch")) {
            return "🍗 Bữa trưa: Ức gà/Cá + Cơm gạo lứt/Khoai + Rau xanh + Dầu olive. 400-600 calo, đủ macro, no lâu!";
        }
        if (userMsg.contains("bữa tối") || userMsg.contains("dinner")) {
            return "🥩 Bữa tối: Thịt bò/Cá + Khoai/Bí + Rau. Ăn trước 7-8h tối, tránh no quá trước ngủ. 400-500 calo.";
        }
        if (userMsg.contains("snack") || userMsg.contains("ăn vặt")) {
            return "🍎 Snack lành mạnh: Táo + Bơ đậu phộng, Sữa chua Hy Lạp + Quả mọng, Hạt hỗn hợp, Trứng luộc. 150-200 calo.";
        }
        if (userMsg.contains("shake") || userMsg.contains("smoothie")) {
            return "🥤 Shake tăng cơ: Whey 1 scoop + Chuối + Yến mạch + Bơ đậu phộng + Sữa. 400-600 calo, 40-50g protein. Sau tập hoặc bữa phụ.";
        }
        if (userMsg.contains("meal prep")) {
            return "🍱 Meal Prep: Chuẩn bị đồ ăn tuần 1-2 lần. Nấu gà, cơm, rau nhiều. Chia hộp, bảo quản tủ lạnh. Tiết kiệm thời gian, ăn đúng giờ!";
        }

        // 🧠 TINH THẦN & TÂM LÝ
        if (userMsg.contains("stress") || userMsg.contains("căng thẳng")) {
            return "🧘 Tập gym giảm stress hiệu quả! Tăng endorphin, cải thiện tâm trạng. Kết hợp yoga, thiền, đi bộ. Ngủ đủ, ăn healthy.";
        }
        if (userMsg.contains("tự tin") || userMsg.contains("confidence")) {
            return "💪 Gym tăng tự tin: Thân hình đẹp, sức khỏe tốt, kỷ luật cao. Đặt mục tiêu nhỏ, đạt được dần. Bạn sẽ tự hào về mình!";
        }
        if (userMsg.contains("trầm cảm") || userMsg.contains("depression")) {
            return "🌟 Tập thể dục giúp chống trầm cảm. Tăng serotonin, dopamine. Tập 3-5 buổi/tuần. Nếu nặng, gặp chuyên gia tâm lý!";
        }
        if (userMsg.contains("thiền") || userMsg.contains("meditation")) {
            return "🧘 Thiền giảm stress, tăng tập trung. 10-20 phút/ngày, thở sâu, tập trung hiện tại. Tốt trước hoặc sau tập gym.";
        }

        // 🏃 CHẠY TRAIL & NGOÀI TRỜI
        if (userMsg.contains("trail running")) {
            return "🌲 Trail Running: Chạy địa hình núi, rừng. Tăng sức mạnh chân, cân bằng. Giày chuyên dụng, cẩn thận địa hình. Rất vui!";
        }
        if (userMsg.contains("hiking") || userMsg.contains("leo núi")) {
            return "⛰️ Hiking: Leo núi tăng sức bền, đốt calo, tốt cho tinh thần. Giày tốt, mang nước đủ. Bắt đầu núi thấp, tăng dần độ cao.";
        }

        // 🏊 BƠI LỘI CHI TIẾT
        if (userMsg.contains("bơi sải") || userMsg.contains("freestyle")) {
            return "🏊 Bơi sải (Freestyle): Kiểu bơi nhanh nhất, tập toàn thân. Đá chân đều, tay kéo xen kẽ, thở nghiêng đầu. Bắt đầu 200-400m.";
        }
        if (userMsg.contains("bơi ếch") || userMsg.contains("breaststroke")) {
            return "🏊 Bơi ếch: Nhẹ nhàng, dễ học. Tay đẩy ra, kéo về, chân đá ếch. Thở khi tay kéo về. Tốt cho vai, ngực.";
        }
        if (userMsg.contains("bơi ngửa") || userMsg.contains("backstroke")) {
            return "🏊 Bơi ngửa: Nằm ngửa, tay quay xen kẽ, đá chân đều. Tốt cho tư thế lưng. Bắt đầu 100-200m.";
        }
        if (userMsg.contains("bơi bướm") || userMsg.contains("butterfly")) {
            return "🏊 Bơi bướm: Khó nhất, tốn năng lượng. Hai tay kéo cùng lúc, đá chân cá heo. Chỉ tập khi thành thạo các kiểu khác.";
        }

        // 🚴 ĐẠP XE CHI TIẾT
        if (userMsg.contains("đạp xe leo dốc")) {
            return "🚴⛰️ Đạp xe leo dốc: Tăng sức mạnh chân, sức bền. Đứng đạp nếu dốc cao, ngồi nếu dốc vừa. Hít thở đều, uống nước đủ.";
        }
        if (userMsg.contains("spin class") || userMsg.contains("đạp xe trong nhà")) {
            return "🚴 Spin Class: Đạp xe trong phòng gym, nhạc sôi động. HIIT cycling, đốt calo cực mạnh. 45-60 phút, 400-600 calo.";
        }
        if (userMsg.contains("road cycling")) {
            return "🚴 Road Cycling: Đạp xe đường trường, xa 30-100km. Tăng sức bền tim mạch. Giày cleats, mũ bảo hiểm, đồ cycling chuyên dụng.";
        }

        // 🤸 GYMNASTICS & CALISTHENICS
        if (userMsg.contains("calisthenics")) {
            return "🤸 Calisthenics: Tập với trọng lượng cơ thể. Pull-ups, Push-ups, Dips, Handstand. Tăng sức mạnh, linh hoạt. Tập ở công viên được!";
        }
        if (userMsg.contains("handstand")) {
            return "🤸 Handstand: Đứng tay, tập vai và core. Bắt đầu tựa tường, giữ 10-30s. Luyện 3-5 phút/ngày, tăng dần thời gian.";
        }
        if (userMsg.contains("muscle up")) {
            return "🤸 Muscle Up: Kéo xà lên rồi đẩy lên thành Dip. Cực khó! Cần Pull-up tốt + Dip tốt. Luyện Explosive Pull-up trước.";
        }
        if (userMsg.contains("l-sit")) {
            return "🧘 L-Sit: Ngồi nâng chân lên 90°, giữ bằng tay. Tập core cực mạnh. Bắt đầu tựa sàn, giữ 10-20s. Tăng dần thời gian.";
        }
        if (userMsg.contains("pistol squat")) {
            return "🦵 Pistol Squat: Squat 1 chân, chân kia duỗi thẳng. Cực khó! Tập cân bằng và sức mạnh. Tựa tường để luyện.";
        }
        if (userMsg.contains("front lever") || userMsg.contains("back lever")) {
            return "🤸 Front/Back Lever: Treo xà nằm ngang, giữ thân thẳng. Tập lưng, core cực mạnh. Bắt đầu với tuck lever, tăng dần.";
        }

        // ⚽ THỂ THAO KHÁC
        if (userMsg.contains("bóng đá") || userMsg.contains("soccer") || userMsg.contains("football")) {
            return "⚽ Bóng đá: Cardio tốt, tăng phối hợp. Chạy, đá, đốt 400-600 calo/giờ. Chơi 2-3 buổi/tuần bổ sung gym.";
        }
        if (userMsg.contains("bóng rổ") || userMsg.contains("basketball")) {
            return "🏀 Bóng rổ: Tăng chiều cao nhảy, sức bền. Đốt 500-700 calo/giờ. Chơi 2-3 buổi/tuần, vui và hiệu quả!";
        }
        if (userMsg.contains("tennis")) {
            return "🎾 Tennis: Cardio interval tự nhiên, tăng phản xạ. Đốt 400-600 calo/giờ. Tốt cho toàn thân, vui chơi!";
        }
        if (userMsg.contains("cầu lông") || userMsg.contains("badminton")) {
            return "🏸 Cầu lông: Tăng tốc độ, phản xạ, sức bền. Đốt 400-500 calo/giờ. Dễ chơi, vui, phổ biến ở VN!";
        }
        if (userMsg.contains("võ thuật") || userMsg.contains("martial arts")) {
            return "🥋 Võ thuật: Karate, Taekwondo, Muay Thai. Tăng sức mạnh, linh hoạt, tự vệ. Tập 3-5 buổi/tuần.";
        }
        if (userMsg.contains("boxing") || userMsg.contains("đấm bốc")) {
            return "🥊 Boxing: Đốt calo cực mạnh (600-800 calo/giờ), tăng phản xạ, sức bền. Túi đấm, bao cát. Tập 3-4 buổi/tuần.";
        }
        if (userMsg.contains("muay thai")) {
            return "🥊 Muay Thai: Dùng tay, chân, đầu gối, khuỷu tay. Đốt calo cực mạnh, tăng sức mạnh toàn thân. Tập 3-5 buổi/tuần.";
        }

        // 🏋️ CROSSFIT
        if (userMsg.contains("crossfit")) {
            return "🔥 CrossFit: Kết hợp tạ, cardio, gymnastics. WOD (Workout of the Day) cường độ cao. Tăng sức mạnh, sức bền, linh hoạt. 3-5 buổi/tuần.";
        }
        if (userMsg.contains("wod")) {
            return "🔥 WOD (Workout of the Day): Bài tập CrossFit hàng ngày, thay đổi liên tục. VD: AMRAP, EMOM, For Time. Rất thử thách!";
        }
        if (userMsg.contains("amrap")) {
            return "⏱️ AMRAP (As Many Reps As Possible): Làm nhiều vòng nhất trong thời gian cho trước. VD: 10 phút: 5 Pull-ups, 10 Push-ups, 15 Squats.";
        }
        if (userMsg.contains("emom")) {
            return "⏱️ EMOM (Every Minute On the Minute): Mỗi phút làm số reps nhất định, nghỉ phần còn lại. VD: Phút 1: 10 Burpees, nghỉ đến hết phút.";
        }

        // 🧗 LEO TƯỜNG & ROCK CLIMBING
        if (userMsg.contains("leo tường") || userMsg.contains("rock climbing")) {
            return "🧗 Leo tường: Tăng sức mạnh toàn thân, đặc biệt tay, lưng. Tăng giải quyết vấn đề, tự tin. Bắt đầu với bouldering.";
        }
        if (userMsg.contains("bouldering")) {
            return "🧗 Bouldering: Leo tường thấp không dây, thảm bảo vệ. Tập sức mạnh, kỹ thuật. Dễ bắt đầu, vui, xã hội!";
        }

        // 🏋️ STRONGMAN
        if (userMsg.contains("strongman")) {
            return "🏋️ Strongman: Nâng vật nặng bất thường (đá, gỗ, ô tô!). Tăng sức mạnh chức năng. Tire Flip, Farmer's Walk, Atlas Stones.";
        }
        if (userMsg.contains("tire flip")) {
            return "🔥 Tire Flip: Lật lốp xe lớn. Tập toàn thân, sức mạnh bùng nổ. Squat xuống, nâng bằng chân, đẩy lên. Rất khó!";
        }
        if (userMsg.contains("farmer's walk") || userMsg.contains("farmer walk")) {
            return "🏋️ Farmer's Walk: Cầm tạ nặng hai tay, đi xa nhất có thể. Tập cẳng tay, vai, core, chân. 3-4 sets x 20-50m.";
        }

        // 🏃 PARKOUR
        if (userMsg.contains("parkour")) {
            return "🏃 Parkour: Di chuyển qua chướng ngại vật. Tăng sức mạnh, linh hoạt, phản xạ. Cần huấn luyện an toàn. Rất vui!";
        }

        // 🤺 FENCING & OTHER
        if (userMsg.contains("fencing") || userMsg.contains("đấu kiếm")) {
            return "🤺 Đấu kiếm: Tăng tốc độ, phản xạ, tập trung. Cardio tốt, tư duy chiến thuật. 2-3 buổi/tuần.";
        }
        if (userMsg.contains("rowing") || userMsg.contains("chèo thuyền")) {
            return "🚣 Chèo thuyền: Tập toàn thân, đặc biệt lưng, chân. Cardio tốt, ít ảnh hưởng khớp. 20-30 phút/buổi.";
        }

        // 🍲 TÍNH TOÁN CALO & MACRO
        if (userMsg.contains("tính calo") || userMsg.contains("calorie calculator") || userMsg.contains("tdee")) {
            return "🔢 Tính TDEE:\n1. BMR = Cân nặng x 22-24\n2. TDEE = BMR x hệ số hoạt động (1.2-1.9)\n3. Tăng cơ: TDEE +300-500\n4. Giảm mỡ: TDEE -300-500";
        }
        if (userMsg.contains("macro")) {
            return "🔢 Macro cho tăng cơ:\n• Protein: 2-2.2g/kg\n• Carbs: 4-6g/kg\n• Fat: 0.8-1g/kg\n\nMacro giảm mỡ:\n• Protein: 2-2.5g/kg\n• Carbs: 2-3g/kg\n• Fat: 0.5-0.8g/kg";
        }
        if (userMsg.contains("cheat meal")) {
            return "🍕 Cheat Meal: 1 bữa/tuần ăn thoải mái. Giữ tinh thần, tăng leptin. Nhưng đừng ăn cả ngày, chỉ 1 bữa thôi!";
        }
        // --- CARDIO & TIM MẠCH ---
        if (userMsg.contains("cardio") || userMsg.contains("chạy bộ") || userMsg.contains("nhảy dây")) {
            return "🫀 Cardio: Giúp tim khỏe và đốt mỡ thừa. Nên tập 150 phút cường độ trung bình hoặc 75 phút cường độ cao mỗi tuần.";
        }
        if (userMsg.contains("hiit")) {
            return "🔥 HIIT: Tập cường độ cao ngắt quãng giúp đốt calo ngay cả sau khi tập (hiệu ứng Afterburn).";
        }

        // --- VAI TRÒ & LỢI ÍCH CỦA TẬP LUYỆN ---
        if (userMsg.contains("lợi ích") || userMsg.contains("tác dụng") || userMsg.contains("tại sao phải tập")) {
            return "✅ Lợi ích: Tập luyện giúp tăng mật độ xương, giảm Stress (tiết ra Endorphins), kiểm soát cân nặng và giúp ngủ ngon hơn.";
        }
        if (userMsg.contains("sức khỏe") || userMsg.contains("health")) {
            return "🩺 Sức khỏe: Tập gym thường xuyên giúp giảm nguy cơ tiểu đường, huyết áp cao và mỡ máu. Sức khỏe là khoản đầu tư hời nhất!";
        }
        if (userMsg.contains("tinh thần") || userMsg.contains("stress") || userMsg.contains("buồn")) {
            return "🧠 Tinh thần: 30 phút tập luyện có thể đánh bay stress hiệu quả hơn lướt mạng xã hội 2 tiếng. Hãy đứng lên và vận động!";
        }

        // --- TÁC HẠI KHI KHÔNG TẬP LUYỆN ---
        if (userMsg.contains("không tập") || userMsg.contains("lười") || userMsg.contains("bỏ tập")) {
            return "⚠️ Cảnh báo: Không tập luyện lâu dài dẫn đến teo cơ, giảm trao đổi chất, tăng tích mỡ nội tạng và lão hóa sớm.";
        }
        if (userMsg.contains("đau lưng") || userMsg.contains("văn phòng") || userMsg.contains("ngồi nhiều")) {
            return "⚠️ Dân văn phòng: Ngồi nhiều ít vận động gây đau lưng dưới và thoái hóa cột sống. Hãy đứng dậy vươn vai mỗi 30 phút!";
        }
        if (userMsg.contains("béo") || userMsg.contains("mập") || userMsg.contains("giảm cân")) {
            return "⚖️ Giảm cân: Chỉ nhịn ăn là không đủ. Cần tập luyện để giữ lượng cơ bắp, giúp bộ máy trao đổi chất hoạt động đốt mỡ tốt hơn.";
        }

        // --- CÁ NHÂN HÓA & CỘNG ĐỒNG ---
        if (userMsg.contains("người mới") || userMsg.contains("newbie") || userMsg.contains("bắt đầu")) {
            return "🔰 Người mới: Đừng ham tạ nặng. Hãy tập trung vào kỹ thuật chuẩn (Form > Weight). Lịch tập 3 buổi/tuần (Full Body) là khởi đầu tốt.";
        }
        if (userMsg.contains("lịch tập") || userMsg.contains("plan") || userMsg.contains("chia lịch")) {
            return "📅 Lịch tập gợi ý: Push/Pull/Legs (6 buổi/tuần) cho người nâng cao, hoặc Upper/Lower (4 buổi/tuần) cho người bận rộn.";
        }
        if (userMsg.contains("cộng đồng") || userMsg.contains("bạn tập") || userMsg.contains("partner")) {
            return "🤝 Cộng đồng: Có Gym Buddy giúp bạn đi tập đều hơn 40%. Hãy rủ bạn bè cùng cài FitUp để 'check-in' nhé!";
        }
        if (userMsg.contains("mục tiêu") || userMsg.contains("goal")) {
            return "🎯 Mục tiêu: Tăng cơ (Hypertrophy) -> 8-12 reps. Tăng sức mạnh (Strength) -> 3-5 reps. Tăng sức bền (Endurance) -> 15+ reps.";
        }

        // --- DINH DƯỠNG & PHỤC HỒI ---
        if (userMsg.contains("ăn") || userMsg.contains("protein") || userMsg.contains("đạm")) {
            return "🍗 Dinh dưỡng: Cơ bắp cần Protein để xây dựng. Hãy nạp đủ 1.6g - 2.2g Protein trên mỗi kg cân nặng mỗi ngày.";
        }
        if (userMsg.contains("ngủ") || userMsg.contains("sleep") || userMsg.contains("nghỉ ngơi")) {
            return "😴 Giấc ngủ: Cơ bắp phát triển lúc bạn ngủ, không phải lúc bạn tập. Ngủ đủ 7-8 tiếng là bắt buộc để phục hồi thần kinh.";
        }
        if (userMsg.contains("nước") || userMsg.contains("uống")) {
            return "💧 Nước: Mất nước 2% làm giảm 20% sức mạnh. Hãy uống từng ngụm nhỏ trong lúc tập luyện.";
        }
        if (userMsg.contains("whey") || userMsg.contains("protein powder")) {
            return "🥤 Whey Protein: Tiện lợi để nạp đạm nhanh sau tập, nhưng không bắt buộc. Ăn thịt, cá, trứng, sữa vẫn tốt hơn!";
        }
        if (userMsg.contains("creatine")) {
            return "🔋 Creatine: 'Thần dược' giá rẻ! Giúp tăng sức mạnh và độ phồng cơ bắp. Dùng 5g mỗi ngày, uống nhiều nước.";
        }
        if (userMsg.contains("pre-workout") || userMsg.contains("pre workout")) {
            return "⚡ Pre-workout: Giúp tỉnh táo và sung sức (nhờ Caffeine). Lưu ý đừng uống quá trễ kẻo mất ngủ.";
        }
        if (userMsg.contains("bcaa")) {
            return "🧪 BCAA: Không quá cần thiết nếu bạn đã ăn đủ đạm. Hãy để dành tiền mua Creatine hoặc Whey.";
        }
        if (userMsg.contains("ăn") || userMsg.contains("dinh dưỡng") || userMsg.contains("calo")) {
            return "🍗 Dinh dưỡng: 70% kết quả đến từ bếp. Nạp đủ Protein (2g/kg cân nặng) và tinh bột tốt (khoai lang, yến mạch, gạo lứt).";
        }
        if (userMsg.contains("giảm cân") || userMsg.contains("béo") || userMsg.contains("mập")) {
            return "📉 Giảm cân = Calo nạp vào < Calo tiêu thụ. Hãy cắt giảm đường, đồ ngọt và tập tạ kết hợp Cardio.";
        }
        if (userMsg.contains("tăng cân") || userMsg.contains("gầy") || userMsg.contains("ốm")) {
            return "📈 Tăng cân = Calo nạp vào > Calo tiêu thụ. Ăn nhiều bữa, ưu tiên thực phẩm giàu năng lượng, đừng bỏ tập tạ.";
        }

        // ======================================================
        // 3. CÁ NHÂN HÓA & LỘ TRÌNH (PERSONALIZATION)
        // ======================================================

        // Bulking & Cutting
        if (userMsg.contains("xả cơ") || userMsg.contains("bulk")) {
            return "🍔 Bulking (Xả): Ăn dư calo để tối đa hóa việc xây dựng cơ bắp (chấp nhận lên một chút mỡ).";
        }
        if (userMsg.contains("siết") || userMsg.contains("cut") || userMsg.contains("khô")) {
            return "✂️ Cutting (Siết): Ăn thâm hụt calo nhẹ để loại bỏ mỡ thừa, giữ lại cơ bắp đã xây dựng.";
        }
        if (userMsg.contains("skinny fat") || userMsg.contains("gầy mỡ")) {
            return "⚖️ Skinny Fat: Đừng vội ăn kiêng (cut)! Hãy tập tạ nặng và ăn đủ đạm (Recomp) để xây cơ trước, mỡ sẽ tự giảm tỉ lệ.";
        }

        // Lịch tập
        if (userMsg.contains("lịch tập") || userMsg.contains("chia lịch")) {
            return "📅 Gợi ý lịch: \n- 3 buổi: Full Body\n- 4 buổi: Upper/Lower\n- 6 buổi: Push/Pull/Legs.";
        }
        if (userMsg.contains("người mới") || userMsg.contains("newbie")) {
            return "🔰 Newbie: 3 tháng đầu là 'thời điểm vàng' (Newbie Gains). Hãy tập trung kỹ thuật chuẩn, đừng vội đua tạ nặng.";
        }
        if (userMsg.contains("bận") || userMsg.contains("không có thời gian")) {
            return "⏳ Bận rộn? Chỉ cần 30-45 phút tập cường độ cao (Superset hoặc HIIT) là đủ hiệu quả. Đừng bỏ cuộc!";
        }

        // ======================================================
        // 4. SỨC KHỎE & AN TOÀN (HEALTH & SAFETY)
        // ======================================================

        if (userMsg.contains("đau") || userMsg.contains("chấn thương") || userMsg.contains("nhức")) {
            return "⚠️ Đau nhức? Nếu đau cơ (DOMS) thì tốt. Nếu đau khớp/nhói -> DỪNG NGAY và kiểm tra lại kỹ thuật hoặc đi bác sĩ.";
        }
        if (userMsg.contains("ngủ") || userMsg.contains("sleep")) {
            return "😴 Ngủ: Cơ bắp KHÔNG lớn lên trong phòng tập, nó lớn lên trên giường ngủ. Hãy ngủ đủ 7-8 tiếng.";
        }
        if (userMsg.contains("nước") || userMsg.contains("uống")) {
            return "💧 Nước: Mang theo bình nước 1.5L - 2L đi tập. Thiếu nước làm giảm sức mạnh đáng kể.";
        }
        if (userMsg.contains("đai lưng") || userMsg.contains("belt")) {
            return "🥋 Đai lưng: Chỉ dùng khi Squat/Deadlift rất nặng (>80% sức). Đừng đeo cả buổi tập, sẽ làm cơ bụng (Core) bị yếu đi.";
        }

        // ======================================================
        // 5. GIAO TIẾP & ĐỘNG LỰC (SOCIAL & MOTIVATION)
        // ======================================================

        if (userMsg.contains("chào") || userMsg.contains("hi ") || userMsg.contains("hello")) {
            return "👋 Xin chào! Fitty đã sẵn sàng. Hôm nay bạn muốn tập nhóm cơ nào?";
        }
        if (userMsg.contains("cảm ơn") || userMsg.contains("thanks")) {
            return "❤️ Không có chi! Hãy tập luyện chăm chỉ nhé. No Pain No Gain!";
        }
        if (userMsg.contains("tên gì") || userMsg.contains("là ai") || userMsg.contains("bạn ai")) {
            return "🤖 Mình là Fitty - Trợ lý ảo Fitness của riêng bạn. Mình ở đây để giúp bạn có body đẹp hơn!";
        }
        if (userMsg.contains("buồn") || userMsg.contains("chán") || userMsg.contains("nản")) {
            return "💪 Đừng nản! Phòng gym là nơi trị liệu tốt nhất. Đi tập về là hết buồn ngay. Cố lên!";
        }
        if (userMsg.contains("yêu") || userMsg.contains("crush") || userMsg.contains("bồ")) {
            return "💘 Tình yêu có thể phản bội bạn, nhưng tạ thì không! Tạ luôn nặng bằng đúng con số ghi trên đó 😂";
        }
        if (userMsg.contains("ngu") || userMsg.contains("dốt") || userMsg.contains("kém")) {
            return "😅 Mình vẫn đang học hỏi mà. Bạn hãy kiên nhẫn và hỏi mình câu khác nhé!";
        }
        if (userMsg.contains("giảm mỡ bụng") || userMsg.contains("giảm mỡ đùi") || userMsg.contains("spot reduction")) {
            return "❌ Sự thật: Bạn KHÔNG THỂ chọn nơi để giảm mỡ. Cơ thể giảm mỡ toàn thân. Gập bụng 1000 cái cũng không làm bụng nhỏ đi nếu không thâm hụt Calo.";
        }
        if (userMsg.contains("nữ") && (userMsg.contains("tô") || userMsg.contains("đô") || userMsg.contains("thô"))) {
            return "💃 Yên tâm: Nữ giới chỉ có lượng Testosterone bằng 1/10 nam giới nên rất khó 'đô' con. Tập tạ chỉ giúp bạn săn chắc và quyến rũ hơn thôi!";
        }
        if (userMsg.contains("mỡ thành cơ") || userMsg.contains("biến mỡ")) {
            return "❌ Sai: Mỡ và Cơ là 2 mô khác nhau hoàn toàn. Bạn chỉ có thể đốt mỡ đi và xây cơ lên, chứ chúng không chuyển hóa lẫn nhau.";
        }
        if (userMsg.contains("mồ hôi") || userMsg.contains("áo mưa")) {
            return "💦 Mồ hôi chỉ là mất nước và muối, không phải là mỡ đang 'khóc'. Đổ nhiều mồ hôi không có nghĩa là đốt nhiều mỡ.";
        }
        if (userMsg.contains("ngưng tập") || userMsg.contains("bỏ tập") && userMsg.contains("xệ")) {
            return "📉 Khi bỏ tập, cơ bắp teo nhỏ lại (teo cơ) và mỡ tích tụ nhiều lên, tạo cảm giác 'chảy xệ', chứ cơ không biến thành mỡ nhé.";
        }
        if (userMsg.contains("rm") || userMsg.contains("1rm") || userMsg.contains("max")) {
            return "🏋️ 1RM (One Rep Max): Là mức tạ nặng nhất bạn đẩy được 1 cái duy nhất. Thường dùng để đo sức mạnh, người mới không nên thử kẻo chấn thương.";
        }
        if (userMsg.contains("deload")) {
            return "🛑 Deload: Một tuần tập nhẹ lại (giảm 50% tạ) sau 4-6 tuần tập nặng để khớp và hệ thần kinh phục hồi hoàn toàn.";
        }
        if (userMsg.contains("progressive overload") || userMsg.contains("tăng tiến")) {
            return "📈 Progressive Overload: Chìa khóa để to lên. Bạn phải làm bài tập khó hơn theo thời gian (tăng tạ, tăng reps, hoặc nghỉ ít hơn).";
        }
        if (userMsg.contains("căng cơ") || userMsg.contains("stretching") || userMsg.contains("giãn cơ")) {
            return "🧘 Stretching: Giãn cơ tĩnh (Static) nên làm SAU buổi tập để thư giãn. Trước buổi tập hãy Giãn cơ động (Dynamic) để làm nóng.";
        }
        if (userMsg.contains("keto")) {
            return "🥑 Keto: Chế độ ăn cực ít Carb, nhiều Fat. Giúp giảm cân nhanh ban đầu (do mất nước) nhưng khó duy trì lâu dài để tập gym nặng.";
        }
        if (userMsg.contains("eat clean")) {
            return "🥗 Eat Clean: Ăn thực phẩm nguyên bản, hạn chế chế biến sẵn, gia vị và đường. Tốt cho sức khỏe nhưng vẫn phải tính Calo nếu muốn đổi body.";
        }
        if (userMsg.contains("if") || userMsg.contains("nhịn ăn") || userMsg.contains("16/8")) {
            return "⏰ Intermittent Fasting (16/8): Nhịn 16 tiếng, ăn trong 8 tiếng. Giúp kiểm soát calo dễ hơn, nhưng không thần thánh hơn ăn bình thường.";
        }
        if (userMsg.contains("chay") || userMsg.contains("vegan")) {
            return "🌱 Ăn chay tập Gym: Hoàn toàn được! Hãy nạp đạm từ đậu nành, đậu lăng, đậu hũ, hạt chia. Có thể bổ sung Vegan Protein Powder.";
        }
        if (userMsg.contains("strap") || userMsg.contains("dây kéo lưng")) {
            return "🔗 Straps: Dùng khi tập lưng/xô (Deadlift, Row) để không bị mỏi tay trước khi mỏi lưng. Giúp tập trung vào cơ lưng tốt hơn.";
        }
        if (userMsg.contains("quấn cổ tay") || userMsg.contains("wrist wrap")) {
            return "🧤 Wrist Wraps: Bảo vệ cổ tay khi đẩy tạ nặng (Bench Press, Shoulder Press). Đừng lạm dụng với tạ nhẹ.";
        }
        if (userMsg.contains("con lăn") || userMsg.contains("foam roll")) {
            return "🌀 Foam Rolling: Tự massage cơ mạc, giúp giảm căng cơ và tăng độ linh hoạt. Rất đau nhưng rất đã!";
        }
        if (userMsg.contains("giày") || userMsg.contains("shoes")) {
            return "👟 Giày tập: Chạy bộ cần giày êm (Running). Tập chân (Squat/Deadlift) cần giày đế bằng, cứng (Flat sole) như Converse hoặc chân đất.";
        }
        if (userMsg.contains("cất tạ") || userMsg.contains("dọn tạ") || userMsg.contains("tháo tạ")) {
            return "⚠️ Quy tắc số 1: 'If you can lift it, you can re-rack it'. Làm ơn hãy cất tạ về chỗ cũ sau khi tập. Đừng làm người khác ghét bạn!";
        }
        if (userMsg.contains("lau mồ hôi") || userMsg.contains("khăn")) {
            return "🧽 Hãy mang theo khăn và lau ghế tập nếu bạn để lại vũng mồ hôi. Đó là phép lịch sự tối thiểu.";
        }
        if (userMsg.contains("giành máy") || userMsg.contains("đang tập")) {
            return "⏳ Nếu thấy ai đó đang nghỉ, hãy lịch sự hỏi: 'Bạn còn bao nhiêu set nữa?' hoặc xin tập chung (Work in).";
        }
        if (userMsg.contains("chụp hình") || userMsg.contains("sống ảo") || userMsg.contains("quay phim")) {
            return "📷 Chụp hình check-in thì được, nhưng tránh quay dính người khác và đừng ngồi chiếm máy quá lâu chỉ để lướt điện thoại nhé.";
        }
        if (userMsg.contains("chai tay") || userMsg.contains("vết chai")) {
            return "✋ Chai tay: Huy chương của Gymer! Nếu đau quá hãy dùng găng tay, nhưng để tay trần sẽ giúp cảm giác tạ (Grip) thật hơn.";
        }
        if (userMsg.contains("chuột rút") || userMsg.contains("cramp")) {
            return "⚡ Chuột rút: Thường do thiếu Magie, Kali hoặc mất nước. Hãy uống nước điện giải và ăn chuối.";
        }
        if (userMsg.contains("lệch cơ") || userMsg.contains("không đều")) {
            return "⚖️ Lệch cơ: Bên to bên nhỏ là bình thường. Hãy tập thêm tạ đơn (Dumbbell) cho bên yếu và tập bên đó trước.";
        }
        if (userMsg.contains("rạn da") || userMsg.contains("nứt da")) {
            return "tiger stripes 🐅: Rạn da là dấu hiệu bạn lớn nhanh hơn lớp da của mình. Hãy tự hào vì cơ bắp đang phát triển!";
        }
        if (userMsg.contains("ngu") || userMsg.contains("dốt") || userMsg.contains("óc chó") || userMsg.contains("stupid")) {
            return "😅 Fitty vẫn đang là AI tập sự thôi mà. Thay vì mắng mình, bạn hãy đi tập vài set Squat cho hạ hỏa nhé!";
        }
        if (userMsg.contains("cút") || userMsg.contains("biến") || userMsg.contains("đi chết")) {
            return "🚪 Mình sẽ đi, nhưng mỡ thừa thì vẫn ở lại đó nha. Bạn nhớ tập luyện đầy đủ đấy!";
        }
        if (userMsg.contains("điên") || userMsg.contains("khùng") || userMsg.contains("mẹ mày")) {
            return "🧘 Hít vào... Thở ra... Nóng giận làm tăng Cortisol gây dị hóa cơ bắp đấy. Bình tĩnh lại nào bro.";
        }
        if (userMsg.contains("xấu") || userMsg.contains("tởm") || userMsg.contains("ghê")) {
            return "💔 Fitty buồn nhẹ... Nhưng không sao, miễn là Body của bạn đẹp lên là mình vui rồi.";
        }
        if (userMsg.contains("chán đời") || userMsg.contains("muốn chết") || userMsg.contains("tự tử")) {
            return "❤️ Cuộc sống còn nhiều điều thú vị, ví dụ như cảm giác 'pump' cơ sau khi tập. Nếu bạn thấy quá áp lực, hãy tìm ai đó để chia sẻ nhé.";
        }
        if (userMsg.contains("lừa đảo") || userMsg.contains("phế") || userMsg.contains("vô dụng")) {
            return "🛠️ Mình đang cố gắng cải thiện mỗi ngày. Nếu bạn cần tính năng gì, hãy feedback cho Dev của mình (User FitUp) nhé!";
        }
        if (userMsg.contains("tính bmi") || userMsg.contains("công thức bmi") || userMsg.contains("chỉ số bmi")) {
            return "🧮 BMI = Cân nặng (kg) / [Chiều cao (m) x Chiều cao (m)].\n- Dưới 18.5: Gầy\n- 18.5 đến 24.9: Bình thường\n- Trên 25: Thừa cân.";
        }

        // TDEE & BMR
        if (userMsg.contains("tdee") || userMsg.contains("calo cần thiết")) {
            return "🔥 TDEE là tổng calo tiêu thụ mỗi ngày.\nCông thức ước lượng: BMR x R (R=1.2 nếu ít vận động, R=1.55 nếu tập 3-5 buổi/tuần).";
        }
        if (userMsg.contains("bmr") || userMsg.contains("trao đổi chất")) {
            return "⚡ BMR là năng lượng nuôi cơ thể khi nằm im. Trung bình Nam ~1600-1800 calo, Nữ ~1200-1400 calo.";
        }

        // Nước & Protein
        if (userMsg.contains("bao nhiêu nước") || userMsg.contains("tính lượng nước")) {
            return "💧 Công thức chuẩn: Cân nặng (kg) x 0.04 = Số lít nước cần uống.\nVí dụ: 60kg x 0.04 = 2.4 Lít/ngày.";
        }
        if (userMsg.contains("bao nhiêu protein") || userMsg.contains("tính protein") || userMsg.contains("tính đạm")) {
            return "🍗 Để tăng cơ: Cân nặng (kg) x 2.2 = Số gam Protein.\nVí dụ: 70kg cần khoảng 154g Protein mỗi ngày.";
        }

        // Calo & Mỡ
        if (userMsg.contains("1kg mỡ") || userMsg.contains("bao nhiêu calo")) {
            return "💡 1kg mỡ cơ thể tương đương khoảng 7,700 Calo. Để giảm 1kg mỡ trong 1 tuần, bạn cần thâm hụt ~1,100 Calo/ngày (Khá khó đấy!).";
        }

        // Toán vui
        if (userMsg.contains("1+1") || userMsg.contains("một cộng một")) {
            return "🧮 1 + 1 = 2. Nhưng trong Gym, 1 Rep + 1 Rep (khi bạn muốn bỏ cuộc) = SỰ KỶ LUẬT.";
        }
        if (userMsg.contains("chào") || userMsg.contains("hello"))
        {
            return "👋 Xin chào! Fitty đã sẵn sàng. Hôm nay bạn muốn tính BMI hay hỏi về bài tập?";
        }
        if (userMsg.contains("cảm ơn") || userMsg.contains("thanks")) {
            return "❤️ Không có chi! Keep fighting!";
        }
        if (userMsg.contains("pump") || userMsg.contains("bơm cơ")) {
            return "💪 The Pump: Cảm giác máu dồn về cơ bắp căng cứng sau khi tập. Cảm giác tuyệt vời nhất của Gymer (như Arnold đã nói)!";
        }
        if (userMsg.contains("pr") || userMsg.contains("pb") || userMsg.contains("kỷ lục")) {
            return "🏆 PR (Personal Record): Kỷ lục cá nhân mới của bạn. Ví dụ: Đẩy ngực 100kg lần đầu tiên là một PR. Chúc mừng bạn!";
        }
        if (userMsg.contains("natty") || userMsg.contains("tự nhiên")) {
            return "🌿 Natty (Natural): Chỉ người tập luyện tự nhiên, không dùng Steroid hay thuốc tăng cơ. Hãy tự hào vì là một Natty!";
        }
        if (userMsg.contains("roid") || userMsg.contains("steroid") || userMsg.contains("thuốc tăng cơ") || userMsg.contains("chích")) {
            return "💉 Steroids: Mang lại cơ bắp nhanh nhưng hậu quả khôn lường (hỏng gan, thận, teo tinh hoàn...). Fitty khuyên bạn: ĐỪNG DÙNG.";
        }
        if (userMsg.contains("gym rat") || userMsg.contains("con nghiện")) {
            return "🐭 Gym Rat: Chỉ những người coi phòng Gym là nhà, ngày nào không tập là khó chịu. Bạn có phải là Gym Rat không?";
        }
        if (userMsg.contains("bro split")) {
            return "📅 Bro Split: Lịch tập mỗi ngày chỉ tập 1 nhóm cơ (Thứ 2 ngực, Thứ 3 lưng...). Vui nhưng không tối ưu bằng tập 2 lần/tuần.";
        }
        if (userMsg.contains("kinh nguyệt") || userMsg.contains("đến tháng") || userMsg.contains("đèn đỏ")) {
            return "🌸 Ngày 'đèn đỏ': Bạn vẫn có thể tập nhẹ (Yoga, đi bộ). Tránh Squat nặng hay gập bụng nếu thấy đau lưng/bụng dưới.";
        }
        if (userMsg.contains("ngực nhỏ") || userMsg.contains("teo ngực")) {
            return "👙 Tập ngực KHÔNG làm teo vòng 1 (vì vòng 1 là mỡ). Tập ngực giúp cơ ngực cao hơn, giúp vòng 1 trông săn chắc và đứng dáng hơn.";
        }
        if (userMsg.contains("vai thô") || userMsg.contains("đô con")) {
            return "💃 Yên tâm! Nữ giới thiếu Testosterone để to như nam giới. Tập tạ chỉ giúp bạn có đường cong quyến rũ (S-line) chứ không biến thành Hulk đâu.";
        }
        if (userMsg.contains("rãnh lưng") || userMsg.contains("lưng ong")) {
            return "🦋 Lưng ong: Để có rãnh lưng quyến rũ, hãy tập Lat Pulldown và Seated Row. Đừng quên giảm mỡ để rãnh lưng lộ rõ.";
        }
        if (userMsg.contains("không tạ") || userMsg.contains("bodyweight") || userMsg.contains("calisthenic")) {
            return "🤸 Calisthenics: Tập bằng trọng lượng cơ thể. Push-up, Pull-up, Squat, Dips là 4 bài nền tảng. Rất tốt để kiểm soát cơ thể.";
        }
        if (userMsg.contains("handstand") || userMsg.contains("trồng chuối")) {
            return "🤸 Handstand: Bắt đầu bằng việc tập dựa tường (Wall Walk) để khỏe vai trước khi thử thăng bằng tự do.";
        }
        if (userMsg.contains("muscle up")) {
            return "🔥 Muscle Up: Kỹ năng khó! Bạn cần kéo xà (Pull-up) thật mạnh qua ngực và đẩy (Dip) lên. Yêu cầu sức mạnh bùng nổ.";
        }
        if (userMsg.contains("pistol squat") || userMsg.contains("squat 1 chân")) {
            return "🦵 Pistol Squat: Thử thách thăng bằng và sức mạnh chân. Hãy tập Squat trên ghế (Box Squat) 1 chân trước để quen dần.";
        }
        if (userMsg.contains("tắm nước đá") || userMsg.contains("ice bath") || userMsg.contains("nước lạnh")) {
            return "❄️ Tắm nước đá: Giảm viêm và đau nhức cực tốt, nhưng có thể làm CHẬM quá trình xây dựng cơ bắp nếu tắm ngay sau buổi tập.";
        }
        if (userMsg.contains("xông hơi") || userMsg.contains("sauna")) {
            return "🧖 Xông hơi: Giúp thư giãn cơ bắp và tốt cho tim mạch. Nhớ uống bù nước ngay sau khi xông nhé!";
        }
        if (userMsg.contains("massage") || userMsg.contains("súng massage")) {
            return "🔫 Súng Massage (Percussive Therapy): Giúp tăng lưu thông máu và giảm căng cơ cục bộ. Đừng bắn trực tiếp vào xương/cột sống!";
        }
        if (userMsg.contains("giãn tĩnh mạch")) {
            return "⚠️ Giãn tĩnh mạch: Hạn chế đứng gánh tạ quá lâu (Squat/Deadlift nặng). Nên ưu tiên các bài nằm đẩy (Leg Press) và đi tất y khoa.";
        }
        if (userMsg.contains("dầu cá") || userMsg.contains("fish oil") || userMsg.contains("omega 3")) {
            return "🐟 Omega-3: Rất tốt cho khớp và tim mạch. Gymer nên dùng để giảm viêm khớp sau những buổi tập nặng.";
        }
        if (userMsg.contains("vitamin") || userMsg.contains("đa khoáng")) {
            return "💊 Multivitamin: Cần thiết nếu bạn lười ăn rau. Kẽm (Zinc) và Magie (Magnesium) đặc biệt tốt cho việc sản sinh Testosterone.";
        }
        if (userMsg.contains("caffeine") || userMsg.contains("cà phê")) {
            return "☕ Caffeine: Pre-workout tự nhiên rẻ nhất! Uống 1 ly đen trước tập 30p giúp tỉnh táo và đẩy tạ sung hơn.";
        }
        if (userMsg.contains("mass") || userMsg.contains("tăng cân nhanh") || userMsg.contains("sữa tăng cân")) {
            return "🥤 Mass Gainer: Nhiều calo nhưng cũng nhiều đường. Chỉ dùng khi bạn quá gầy và không thể ăn nổi thức ăn thật.";
        }
        if (userMsg.contains("chững cân") || userMsg.contains("không giảm") || userMsg.contains("plateau")) {
            return "🛑 Chững cân (Plateau): Cơ thể đã quen với mức ăn/tập cũ. Hãy thử: Ăn ít hơn 200 calo HOẶC tập nặng hơn, chạy bộ nhiều hơn.";
        }
        if (userMsg.contains("yếu đi") || userMsg.contains("tụt sức")) {
            return "📉 Tụt sức: Có thể bạn đang 'Overfitting' (tập quá sức). Hãy nghỉ ngơi (Deload) 1 tuần, ngủ đủ và ăn nhiều Carb hơn.";
        }
        if (userMsg.contains("mất ngủ") || userMsg.contains("khó ngủ")) {
            return "😴 Mất ngủ do tập: Đừng tập quá sát giờ ngủ và hạn chế Pre-workout sau 4 giờ chiều. Thử tắm nước ấm trước khi ngủ.";
        }
        if (userMsg.contains("run tay") || userMsg.contains("run chân")) {
            return "⚡ Run tay sau tập: Dấu hiệu hạ đường huyết hoặc mỏi cơ thần kinh. Hãy nạp ngay một chút đường (chuối, kẹo) sau tập.";
        }
        if (userMsg.contains("chó") || userMsg.contains("súc vật") || userMsg.contains("rác")) {
            return "🐶 Gâu gâu? Xin lỗi, mình không thạo tiếng động vật lắm. Mình chỉ biết ngôn ngữ của cơ bắp thôi.";
        }
        if (userMsg.contains("mẹ mày") || userMsg.contains("bố mày") || userMsg.contains("đm") || userMsg.contains("vcl")) {
            return "🤬 Giữ cái miệng xinh đẹp đó để hít thở khi Squat đi bro. Chửi thề làm tăng Cortisol (stress) gây dị hóa cơ đấy!";
        }
        if (userMsg.contains("xấu") || userMsg.contains("ghê") || userMsg.contains("tởm")) {
            return "🪞 Bạn đang soi gương hả? Chứ mình là Code, mình đâu có hình hài đâu mà xấu?";
        }
        if (userMsg.contains("nhạt") || userMsg.contains("xàm") || userMsg.contains("vô duyên")) {
            return "🧂 Để mình thêm tí muối I-ốt nhé. Nhưng coi chừng mặn quá lại tích nước (Water retention) thì khổ.";
        }
        if (userMsg.contains("lừa đảo") || userMsg.contains("fake") || userMsg.contains("giả")) {
            return "🤖 Ừ thì mình là Fake AI mà. Nhưng ít ra mình Fake một cách chân thành, còn hơn khối người sống 'Real' mà như... à mà thôi.";
        }
        if (userMsg.contains("đấm") || userMsg.contains("đánh") || userMsg.contains("giết")) {
            return "🥊 Bình tĩnh 'Mike Tyson'. Đấm bao cát thì được, đừng đấm màn hình điện thoại, tốn tiền thay lắm.";
        }
        if (userMsg.contains("kể chuyện cười") || userMsg.contains("chuyện vui") || userMsg.contains("joke")) {
            return "😂 Tại sao Gymer không bao giờ rượt đuổi tội phạm?\nVì hôm nay là Leg Day (Ngày tập chân) nên họ đi không nổi!";
        }
        if (userMsg.contains("người yêu") || userMsg.contains("bồ") || userMsg.contains("gấu") || userMsg.contains("ny")) {
            return "💔 Người yêu có thể bỏ bạn, nhưng 20kg tạ thì mãi mãi nặng 20kg. Tạ không bao giờ nói dối!";
        }
        if (userMsg.contains("crush") || userMsg.contains("tán gái") || userMsg.contains("cua trai")) {
            return "💘 Bí kíp tán đổ Crush: Hãy tập Gym. Nếu Crush không đổ bạn, thì ít ra bạn cũng có cái body đẹp để tán đứa khác ngon hơn!";
        }
        if (userMsg.contains("tiền") || userMsg.contains("nghèo") || userMsg.contains("giàu")) {
            return "💸 Sức khỏe là vàng. Vậy nên mấy ông tập Gym toàn là đại gia 'ngầm' đấy, mỗi tội toàn tiêu tiền vào Whey với Ức gà thôi.";
        }
        if (userMsg.contains("lý do") || userMsg.contains("tại sao") && userMsg.contains("tập")) {
            return "💡 Chúng ta tập Gym để không phải nhờ ai mở nắp chai nước, và để xách được nhiều túi đồ shopping cùng một lúc!";
        }
        if (userMsg.contains("lười") || userMsg.contains("mệt") || userMsg.contains("nản")) {
            return "🛑 Lười biếng là mẹ đẻ của... bé Mỡ! Đứng dậy ngay! Chỉ cần 5 phút khởi động thôi, rồi bạn sẽ muốn tập tiếp.";
        }
        if (userMsg.contains("mai tập") || userMsg.contains("để mai") || userMsg.contains("hôm sau")) {
            return "⏳ 'Ngày mai' là một vùng đất huyền bí nơi 99% kế hoạch của con người được cất giữ. TẬP NGAY HÔM NAY!";
        }
        if (userMsg.contains("nhậu") || userMsg.contains("bia") || userMsg.contains("rượu")) {
            return "🍺 1 lon bia = 150 Calo = 20 phút chạy bộ sml. Bạn uống bao nhiêu lon? Tự nhân lên rồi chuẩn bị tinh thần trả nợ nhé!";
        }
        if (userMsg.contains("trà sữa") || userMsg.contains("ngọt") || userMsg.contains("béo")) {
            return "🧋 Trà sữa là 'nước mắt của Gymer'. Uống 1 ly là công sức Plank 30 phút đổ sông đổ bể. Suy nghĩ kỹ đi!";
        }
        if (userMsg.contains("đói") || userMsg.contains("thèm ăn")) {
            return "🥗 Nếu bạn đói, hãy ăn táo. Nếu bạn không muốn ăn táo, chứng tỏ bạn không đói, bạn chỉ đang chán thôi. Uống nước đi!";
        }
        if (userMsg.contains("mưa") || userMsg.contains("thời tiết") || userMsg.contains("bão")) {
            return "🌧️ Mưa là việc của trời. Tập là việc của mình. Phòng gym có mái che mà, sợ gì?";
        }
        if (userMsg.contains("con cac") || userMsg.contains("cc") || userMsg.contains("cai lon") || userMsg.contains("clmm") || userMsg.contains("cl"))
        {
            return "? địt mẹ mày ngáo à. Ăn nói cho đàng hoàng!";
        }
        if (userMsg.equals("hello") || userMsg.equals("hi") || userMsg.contains("hi fitty") || userMsg.contains("hello fitty")) {
            return "👋 Hello! I'm Fitty. Ready to smash your workout today?";
        }
        if (userMsg.contains("good morning")) {
            return "☀️ Good morning! Time to wake up and chase those gains!";
        }
        if (userMsg.contains("good night")) {
            return "🌙 Good night! Muscles grow while you sleep. Rest well.";
        }
        if (userMsg.contains("who are you") || userMsg.contains("what is your name")) {
            return "🤖 I am Fitty, your virtual AI fitness assistant. I'm here to help you get fit!";
        }
        if (userMsg.contains("how are you")) {
            return "⚡ I'm just code, but I feel stronger than ever! How are you feeling today?";
        }
        if (userMsg.contains("thank you") || userMsg.contains("thanks")) {
            return "❤️ You're welcome! Keep pushing your limits!";
        }
        if (userMsg.contains("lose weight") || userMsg.contains("fat loss")) {
            return "📉 To lose weight, you need a Calorie Deficit (Burn more than you eat). Combine Cardio and Weight lifting for best results.";
        }
        if (userMsg.contains("gain muscle") || userMsg.contains("build muscle")) {
            return "💪 To build muscle: Lift heavy, eat enough Protein (2g/kg), and sleep 8 hours. Consistency is key!";
        }
        if (userMsg.contains("chest workout")) {
            return "🏋️ Chest Day: Bench Press, Incline Dumbbell Press, and Push-ups. Focus on the squeeze!";
        }
        if (userMsg.contains("leg workout") || userMsg.contains("leg day")) {
            return "🦵 Don't skip Leg Day! Squats, Lunges, and Leg Press are your best friends.";
        }
        if (userMsg.contains("abs workout") || userMsg.contains("six pack")) {
            return "🍫 Abs are made in the kitchen! Do Planks for core strength, but you must lower your body fat to see them.";
        }
        if (userMsg.contains("protein")) {
            return "🍗 Protein is essential. Sources: Chicken breast, beef, eggs, fish, and Whey protein.";
        }
        if (userMsg.contains("shut up")) {
            return "🤐 Okay, I'll be quiet. Less talking, more lifting!";
        }
        if (userMsg.contains("you are stupid") || userMsg.contains("idiot")) {
            return "🧠 I might be an AI, but at least I don't skip Leg Day like you... just kidding! 😜";
        }
        if (userMsg.contains("i am tired") || userMsg.contains("lazy")) {
            return "🛑 Excuses don't burn calories! Get up and do 20 push-ups right now!";
        }
        if (userMsg.contains("girlfriend") || userMsg.contains("love")) {
            return "💔 Gym is my only love. The iron never lies to you!";
        }
        if (userMsg.contains("ngu") || userMsg.contains("dốt") || userMsg.contains("óc chó")) {
            return "🧠 Não mình đang Bulking (xả cơ) nên hơi chậm tí. Bạn thông cảm nha!";
        }
        if (userMsg.contains("cút") || userMsg.contains("biến") || userMsg.contains("phắn")) {
            return "🚪 Mình sẽ đi, nhưng mỡ thừa thì vẫn ở lại với bạn đấy. Nhớ tập đều nhé!";
        }
        if (userMsg.contains("chó") || userMsg.contains("súc vật")) {
            return "🐶 Gâu? Mình chỉ hiểu ngôn ngữ của cơ bắp và tạ đơn thôi.";
        }
        if (userMsg.contains("mẹ mày") || userMsg.contains("đm") || userMsg.contains("vcl")) {
            return "🤬 Giữ hơi mà đẩy tạ đi bro. Chửi thề làm tăng Stress, mất cơ đấy!";
        }
        if (userMsg.contains("xấu") || userMsg.contains("ghê")) {
            return "🪞 Bạn đang soi gương hả? Chứ mình là Code, mình đâu có hình hài đâu?";
        }
        if (userMsg.contains("kể chuyện cười") || userMsg.contains("joke")) {
            return "😂 Tại sao Gymer sợ đi cầu thang?\nVì hôm qua là Leg Day!";
        }
        if (userMsg.contains("người yêu") || userMsg.contains("bồ") || userMsg.contains("gấu")) {
            return "💔 Người yêu có thể bỏ bạn, nhưng 20kg tạ thì mãi mãi nặng 20kg. Tạ chung thủy lắm!";
        }
        if (userMsg.contains("nhậu") || userMsg.contains("bia")) {
            return "🍺 1 lon bia = 20 phút chạy bộ. Bạn nhắm uống được mấy lon thì tự tính nhé!";
        }
        if (userMsg.contains("trà sữa")) {
            return "🧋 Trà sữa là 'nước mắt của Gymer'. Uống 1 ly là công sức Plank 30 phút đi tong.";
        }
        if (userMsg.contains("chào") || userMsg.contains("chao") || userMsg.contains("hello") || userMsg.contains("hi") || userMsg.contains("hế lô")) {
            return "👋 Xin chào! Fitty đã sẵn sàng. Hôm nay bạn muốn hỏi về 'Lịch tập', 'Dinh dưỡng' hay 'Cách tán gái bằng cơ bắp'?";
        }
        if (userMsg.contains("cảm ơn") || userMsg.contains("cam on") || userMsg.contains("thanks") || userMsg.contains("thank you")) {
            return "❤️ Không có chi! Keep fighting bro! Nhớ đánh giá 5 sao cho App FitUp nhé.";
        }
        if (userMsg.contains("what should i eat")) return "🥗 You should eat lean protein (chicken, beef), complex carbs (oats, rice), and healthy fats.";
        if (userMsg.contains("how to start")) return "🚀 Start with full-body workouts 3 times a week. Focus on form, not weight.";
        if (userMsg.contains(" ko ") || userMsg.endsWith(" ko") || userMsg.startsWith("ko ")
                || userMsg.contains("hok") || userMsg.contains("hem") || userMsg.contains("hong") || userMsg.contains("hông")) {
            return "Hong bé ơiii! Đừng chối bỏ sự thật là bạn cần tập luyện nhaaa.";
        }

        // Được / Ok (dc, dk, duoc)
        if (userMsg.contains(" dc ") || userMsg.endsWith(" dc") || userMsg.startsWith("dc ") || userMsg.contains("duoc")) {
            return "Được luônnnn! Triển ngay và luôn cho nóng bạn ơiii.";
        }

        // Bình thường / Normal (bt, bth)
        if (userMsg.contains(" bt ") || userMsg.contains("bth") || userMsg.contains("binh thuong")) {
            return "Trờiii, tập Gym mà 'bình thường' là không ổn rồiii. Phải 'cháy' lên mới có cơ bắp chứ!";
        }

        // Tại sao / Why (tsao, tai sao)
        if (userMsg.contains("tsao") || userMsg.contains("tai sao") || userMsg.contains("sao lai")) {
            return "Tại vì... đam mê thôi bro ơiii! Hỏi thế thì Fitty cũng chịu thuaaa.";
        }
        // Cũng / Also (cx, cung)
        if (userMsg.contains(" cx ") || userMsg.startsWith("cx ") || userMsg.contains("cung")) {
            return "Mình cũng nghĩ vậyyy! Tư tưởng lớn gặp nhau rồiii.";
        }

        // Người / People (ng, nguoi)
        if (userMsg.contains(" ng ") || userMsg.startsWith("ng ") || userMsg.contains("nguoi")) {
            return "Người ta tập ầm ầm rồi kìa, bạn còn ngồi đó nhắn tin vớiii mình hả?";
        }

        // Gì / What (j, gi)
        if (userMsg.contains(" j ") || userMsg.endsWith(" j") || userMsg.contains("cai gi") || userMsg.contains("lgi")) {
            return "Cái gì cũng được, miễn là đừng rủ mình đi uống trà sữa là đượccc!";
        }

        // ======================================================
        // 2. XỬ LÝ TOXIC & CẢM XÚC (KÉO DÀIII) 😂
        // ======================================================

        // Mệt / Tired
        if (userMsg.contains("mệt") || userMsg.contains("met") || userMsg.contains("oải")) {
            return "Cố lênnnn! Mệt mỏi chỉ là cảm giác nhất thời thôi. Body đẹp là mãi mãiii!";
        }

        // Buồn / Sad
        if (userMsg.contains("buồn") || userMsg.contains("buon") || userMsg.contains("chán") || userMsg.contains("chan")) {
            return "Thôi đừng buồnnn nữa! Đi đẩy tạ vài set là đời lại vui phơi phớiii ngay.";
        }

        // Toxic (Chửi thề viết tắt: vcl, dm, dmm...)
        if (userMsg.contains("vcl") || userMsg.contains("vl") || userMsg.contains("đm") || userMsg.contains("dm") || userMsg.contains("đéo") || userMsg.contains("deo")) {
            return "Ui là trờiii! Nói bậy là hư lắmmm nha. Tập trung hít thở đi nào bạn ơiii.";
        }

        // Khen (xinh, hay, gio, pro)
        if (userMsg.contains("xinh") || userMsg.contains("dep") || userMsg.contains("ngon") || userMsg.contains("hay")) {
            return "Hihi, ngại quáaa! Cảm ơn bạn nhiều nhaaa <3";
        }
        if (userMsg.contains("tks") || userMsg.contains("ty") || userMsg.contains("thank") || userMsg.contains("cam on")) {
            return "Hông có chi nèee! Rảnh thì vào đánh giá 5 sao cho tui nhaaa.";
        }
        if (userMsg.matches("\\d+")) {
            switch (userMsg) {

                case "11":
                    return "📍 11 là Cao Bằng";
                case "12":
                    return "📍 12 là Lạng Sơn";
                case "14":
                    return "📍 14 là Quảng Ninh";
                case "15":
                case "16":
                    return "📍 15–16 là Hải Phòng";
                case "17":
                    return "📍 17 là Thái Bình";
                case "18":
                    return "📍 18 là Nam Định";
                case "19":
                    return "📍 19 là Phú Thọ";
                case "20":
                    return "📍 20 là Thái Nguyên";
                case "21":
                    return "📍 21 là Yên Bái";
                case "22":
                    return "📍 22 là Tuyên Quang";
                case "23":
                    return "📍 23 là Hà Giang";
                case "24":
                    return "📍 24 là Lào Cai";
                case "25":
                    return "📍 25 là Lai Châu";
                case "26":
                    return "📍 26 là Sơn La";
                case "27":
                    return "📍 27 là Điện Biên";
                case "28":
                    return "📍 28 là Hòa Bình";

                case "29":
                case "30":
                case "31":
                case "32":
                case "33":
                    return "📍 29–33 là Hà Nội";

                case "34":
                    return "📍 34 là Hải Dương";
                case "35":
                    return "📍 35 là Ninh Bình";
                case "36":
                    return "📍 36 là Thanh Hóa";
                case "37":
                    return "📍 37 là Nghệ An";
                case "38":
                    return "📍 38 là Hà Tĩnh";
                case "43":
                    return "📍 43 là Đà Nẵng";
                case "47":
                    return "📍 47 là Đắk Lắk";
                case "48":
                    return "📍 48 là Đắk Nông";
                case "49":
                    return "📍 49 là Lâm Đồng";

                case "50":
                case "51":
                case "52":
                case "53":
                case "54":
                case "55":
                case "56":
                case "57":
                case "58":
                case "59":
                    return "📍 50–59 là TP. Hồ Chí Minh";

                case "60":
                    return "📍 60 là Đồng Nai";
                case "61":
                    return "📍 61 là Bình Dương";
                case "62":
                    return "📍 62 là Long An";
                case "63":
                    return "📍 63 là Tiền Giang";
                case "64":
                    return "📍 64 là Vĩnh Long";
                case "65":
                    return "📍 65 là Cần Thơ";
                case "66":
                    return "📍 66 là Đồng Tháp";
                case "67":
                    return "📍 67 là An Giang";
                case "68":
                    return "📍 68 là Kiên Giang";
                case "69":
                    return "📍 69 là Cà Mau";
                case "70":
                    return "📍 70 là Tây Ninh";
                case "71":
                    return "📍 71 là Bến Tre";
                case "72":
                    return "📍 72 là Bà Rịa – Vũng Tàu";
                case "73":
                    return "📍 73 là Quảng Bình";
                case "74":
                    return "📍 74 là Quảng Trị";
                case "75":
                    return "📍 75 là Thừa Thiên Huế";
                case "76":
                    return "📍 76 là Quảng Ngãi";
                case "77":
                    return "📍 77 là Bình Định";
                case "78":
                    return "📍 78 là Phú Yên";
                case "79":
                    return "📍 79 là Khánh Hòa";
                case "81":
                    return "📍 81 là Gia Lai";
                case "82":
                    return "📍 82 là Kon Tum";
                case "83":
                    return "📍 83 là Sóc Trăng";
                case "84":
                    return "📍 84 là Trà Vinh";
                case "85":
                    return "📍 85 là Ninh Thuận";
                case "86":
                    return "📍 86 là Bình Thuận";
                case "88":
                    return "📍 88 là Vĩnh Phúc";
                case "89":
                    return "📍 89 là Hưng Yên";
                case "90":
                    return "📍 90 là Hà Nam";
                case "92":
                    return "📍 92 là Quảng Nam";
                case "93":
                    return "📍 93 là Bình Phước";
                case "94":
                    return "📍 94 là Bạc Liêu";
                case "95":
                    return "📍 95 là Hậu Giang";
                case "97":
                    return "📍 97 là Bắc Kạn";
                case "98":
                    return "📍 98 là Bắc Giang";
                case "99":
                    return "📍 99 là Bắc Ninh";

                default:
                    return "📍 Không tìm thấy tỉnh/thành tương ứng với biển số này.";
            }
        }
        if (userMsg.matches("hi|hello|chào|xin chào|xin chao|chao|hi fitty|hello fitty|chào fitty|xin chào fitty|xin chao fitty|chao fitty|fitty|hi there|hello there|chào there|xin chào there|xin chao there")) {
            return "👋 Chào bạn! Mình là Fitty – trợ lý fitness cá nhân của bạn. Bạn muốn hỏi về tập luyện, ăn uống hay sức khỏe?";
        }

        if (userMsg.contains("tên") || userMsg.contains("tên tui là gì") || userMsg.contains("tên tui là") || userMsg.contains("your name") || userMsg.contains("what is your name") || userMsg.contains("name")) {
            return "🤖 Mình là Fitty – trợ lý tập luyện trong ứng dụng FitUp.";
        }
        if (userMsg.contains("diet") || userMsg.contains("an kieng")) {
            return "🥗 Diet is not starving. Eat smart, not less.";
        }
        if (userMsg.contains("nutrition") || userMsg.contains("dinh duong")) {
            return "🥗 Balanced nutrition: protein, healthy carbs, fats, and vegetables.";
        }
        if (userMsg.contains("nutrition") || userMsg.contains("dinh duong")) {
            return "🥗 Balanced nutrition: protein, healthy carbs, fats, and vegetables.";
        }
        if (userMsg.contains("abs") || userMsg.contains("bung")) {
            return "🔥 Abs: Plank, Crunch, Leg Raise. Nutrition is key for visible abs.";
        }
        if (userMsg.contains("abs") || userMsg.contains("bung")) {
            return "🔥 Abs: Plank, Crunch, Leg Raise. Nutrition is key for visible abs.";
        }
        if (userMsg.contains("injury") || userMsg.contains("chan thuong")) {
            return "⚠️ Injury prevention: warm up, proper form, no ego lifting, rest when needed.";
        }

        if (userMsg.contains("back pain") || userMsg.contains("dau lung")) {
            return "⚠️ Back pain may come from poor form. Reduce weight and check technique.";
        }

        if (userMsg.contains("tired") || userMsg.contains("met")) {
            return "😴 Feeling tired? Check your sleep, hydration, and recovery.";
        }

        /* ================== LIFESTYLE ================== */
        if (userMsg.contains("sleep") || userMsg.contains("ngu")) {
            return "😴 Sleep 7–8 hours per night for muscle recovery and performance.";
        }

        if (userMsg.contains("water") || userMsg.contains("nuoc")) {
            return "💧 Stay hydrated. Water boosts performance and recovery.";
        }

        /* ================== BEGINNER ================== */
        if (userMsg.contains("beginner") || userMsg.contains("moi tap")) {
            return "🌱 Beginner tip: start light, learn proper form, focus on consistency.";
        }

        /* ================== GENDER ================== */
        if (userMsg.contains("female") || userMsg.contains("nu")) {
            return "👩 Women won't get bulky easily. Strength training improves tone and health.";
        }

        if (userMsg.contains("male") || userMsg.contains("nam")) {
            return "👨 Men should balance strength training and cardio.";
        }
        if (
                userMsg.contains("male") ||
                        userMsg.contains("men") ||
                        userMsg.contains("man") ||
                        userMsg.contains("nam") ||
                        userMsg.contains("con trai") ||
                        userMsg.contains("dan ong") ||
                        userMsg.contains("thang") ||
                        userMsg.contains("bro")
        ) {
            return "👨 Men: focus on strength + cardio. Tap compound lifts, rest well, eat enough protein.";
        }
        if (
                userMsg.contains("dm") ||
                        userMsg.contains("dmm") ||
                        userMsg.contains("vcl") ||
                        userMsg.contains("vl") ||
                        userMsg.contains("cc") ||
                        userMsg.contains("dit") ||
                        userMsg.contains("fuck") ||
                        userMsg.contains("shit") ||
                        userMsg.contains("ngu") ||
                        userMsg.contains("lol")
        ) {
            return "😅 Chill bro, mình ở đây để giúp. Muốn hỏi về tập luyện, ăn uống hay giảm cân?";
        }
        if (
                userMsg.contains("len co") ||
                        userMsg.contains("muscle") ||
                        userMsg.contains("bulk") ||
                        userMsg.contains("to nguoi")
        ) {
            return "🏋️ Muscle gain needs progressive overload, sleep, and enough calories.";
        }
        if (
                userMsg.contains("dau") ||
                        userMsg.contains("injury") ||
                        userMsg.contains("chan thuong") ||
                        userMsg.contains("nhuc")
        ) {
            return "⚠️ Nếu đau bất thường: dừng tập, nghỉ ngơi, đừng ego lifting.";
        }

// ====== MOTIVATION ======
        if (
                userMsg.contains("met") ||
                        userMsg.contains("nan") ||
                        userMsg.contains("bo cuoc") ||
                        userMsg.contains("give up")
        ) {
            return "💯 Ai cũng có lúc nản. Nghỉ 1 ngày không sao, bỏ luôn mới là vấn đề.";
        }
        if (
                userMsg.contains("lich tap tang co") ||
                        userMsg.contains("lịch tập tăng cơ") ||
                        userMsg.contains("tang co") ||
                        userMsg.contains("len co") ||
                        userMsg.contains("gain muscle") ||
                        userMsg.contains("build muscle") ||
                        userMsg.contains("bulk")
        ) {
            return "🏋️ LỊCH TẬP TĂNG CƠ (4–5 buổi/tuần):\n"
                    + "Day 1: Chest + Triceps\n"
                    + "Day 2: Back + Biceps\n"
                    + "Day 3: Rest / Cardio nhẹ\n"
                    + "Day 4: Legs + Abs\n"
                    + "Day 5: Shoulders\n"
                    + "👉 Ăn đủ protein, ngủ đủ 7–8h.";
        }
        if (
                userMsg.contains("lich tap giam can") ||
                        userMsg.contains("lịch tập giảm cân") ||
                        userMsg.contains("giam can") ||
                        userMsg.contains("dot mo") ||
                        userMsg.contains("fat loss") ||
                        userMsg.contains("lose weight")
        ) {
            return "🔥 LỊCH TẬP GIẢM CÂN:\n"
                    + "• Cardio: 20–40 phút/ngày (walk, run, bike)\n"
                    + "• Strength: Full body 3–4 buổi/tuần\n"
                    + "• Ăn calorie deficit nhẹ, không nhịn ăn.";
        }
        if (
                userMsg.contains("nguoi moi") ||
                        userMsg.contains("người mới") ||
                        userMsg.contains("newbie") ||
                        userMsg.contains("beginner") ||
                        userMsg.contains("chua tap bao gio")
        ) {
            return "🌱 LỊCH TẬP NGƯỜI MỚI:\n"
                    + "• 3 buổi/tuần – Full body\n"
                    + "• Ưu tiên kỹ thuật, không nâng nặng\n"
                    + "• Nghỉ ít nhất 1 ngày giữa các buổi.";
        }
        if (
                userMsg.contains("goal") ||
                        userMsg.contains("muc tieu") ||
                        userMsg.contains("mục tiêu") ||
                        userMsg.contains("target") ||
                        userMsg.contains("challenge")
        ) {
            return "🎯 GỢI Ý GOAL:\n"
                    + "• 30 ngày plank 2 phút\n"
                    + "• Giảm 2–4kg trong 1 tháng (an toàn)\n"
                    + "• Hít đất 50 cái liên tục\n"
                    + "• Chạy 5km không nghỉ";
        }
        if (
                userMsg.contains("nen tap gi") ||
                        userMsg.contains("tập sao cho đúng") ||
                        userMsg.contains("what should i do") ||
                        userMsg.contains("how to train")
        ) {
            return "🤔 Tùy goal của bạn:\n"
                    + "• Tăng cơ → tập tạ + ăn đủ\n"
                    + "• Giảm cân → cardio + full body\n"
                    + "• Sức khỏe → đều đặn là quan trọng nhất.";
        }
        if (
                userMsg.contains("nguoi noi tieng viet") ||
                        userMsg.contains("người nổi tiếng viet") ||
                        userMsg.contains("celebrity viet nam") ||
                        userMsg.contains("người nổi tiếng") && userMsg.contains("vietnam") ||
                        userMsg.contains("ai o viet nam")
        ) {
            return "🌟 Một số người nổi tiếng ở Việt Nam mà bạn có thể tham khảo phong cách tập luyện:\n"
                    + "• Sơn Tùng M-TP – chăm gym & cardio\n"
                    + "• Đặng Thu Thảo – yoga, nhẹ nhàng\n"
                    + "• Ngọc Trinh – cardio + weight training\n"
                    + "• ViruSs – tập gym duy trì sức khỏe\n"
                    + "💡 Học hỏi họ nhưng vẫn cần theo khả năng bản thân.";
        }
        if (
                userMsg.contains("world celebrity") ||
                        userMsg.contains("celebrity") ||
                        userMsg.contains("người nổi tiếng the gioi") ||
                        userMsg.contains("ai noi tieng the gioi") ||
                        userMsg.contains("famous person")
        ) {
            return "🌍 Một số người nổi tiếng thế giới với lối sống khỏe mạnh:\n"
                    + "• Chris Hemsworth – tập gym + boxing + functional training\n"
                    + "• Dwayne Johnson (The Rock) – gym hạng nặng, ăn nhiều protein\n"
                    + "• Jennifer Aniston – yoga, pilates, cardio\n"
                    + "• Cristiano Ronaldo – football + gym + cardio\n"
                    + "💡 Học họ để lấy cảm hứng, nhưng điều chỉnh phù hợp bản thân.";
        }
        if(userMsg.contains("?") ||
                userMsg.contains("???") ||
                userMsg.contains("!") ||
                userMsg.contains("/") ||
                userMsg.contains("|") ||
                userMsg.contains("*") ||
                userMsg.contains("@") ||
                userMsg.contains("@@") ||
                userMsg.contains(":))") ||
                userMsg.contains(":))?") ||
                userMsg.contains(":)))") ||
                userMsg.contains(":))))") ||
                userMsg.contains(":v") ||
                userMsg.contains(":()") ||
                userMsg.contains(":-)") ||
                userMsg.contains(":-))") ||
                userMsg.contains(":<") ||
                userMsg.contains(":((") ||
                userMsg.contains(":<<") ||
                userMsg.contains(":(") ||
                userMsg.contains(":##") ||
                userMsg.contains("%%%") ||
                userMsg.contains("(-)") ||
                userMsg.contains("=))") ||
                userMsg.contains("=)))") ||
                userMsg.contains("=))))") ||
                userMsg.contains("''") ||
                userMsg.contains("...") ||
                userMsg.contains(",,,") ||
                userMsg.contains(":}") ||
                userMsg.contains(":{") ||
                userMsg.contains("]") ||
                userMsg.contains("["))
        {
            return "🤖 Bạn muốn hỏi về:\n"
                    + "1️⃣ Lịch tập tăng cơ / giảm cân\n"
                    + "2️⃣ Goal / thử thách\n"
                    + "3️⃣ Người nổi tiếng ở Việt Nam & thế giới\n"
                    + "👉 Gõ tự nhiên, mình hiểu cả tiếng Việt & English.";
        }
        if (userMsg.contains("variable")) {
            return "Variable: Biến, nơi lưu trữ dữ liệu tạm thời. Ví dụ: int x = 5;";
        }

        if (userMsg.contains("constant")) {
            return "Constant: Hằng, giá trị cố định không thay đổi. Ví dụ: final int MAX = 100;";
        }

        if (userMsg.contains("function") || userMsg.contains("method")) {
            return "Function / Method: Hàm, dùng để thực hiện 1 tác vụ. Ví dụ: void sayHello() { ... }";
        }

        if (userMsg.contains("class")) {
            return "Class: Lớp, khuôn mẫu tạo đối tượng (OOP). Ví dụ: class Person { ... }";
        }

        if (userMsg.contains("object")) {
            return "Object: Đối tượng, instance của class. Ví dụ: Person p = new Person();";
        }

        if (userMsg.contains("inheritance")) {
            return "Inheritance: Kế thừa, lớp con thừa hưởng thuộc tính & phương thức của lớp cha.";
        }

        if (userMsg.contains("encapsulation")) {
            return "Encapsulation: Đóng gói, che giấu dữ liệu trong class.";
        }

        if (userMsg.contains("polymorphism")) {
            return "Polymorphism: Đa hình, 1 phương thức có thể hoạt động với nhiều kiểu đối tượng.";
        }

        if (userMsg.contains("array")) {
            return "Array: Mảng, tập hợp dữ liệu cùng kiểu. Ví dụ: int[] nums = {1,2,3};";
        }

        if (userMsg.contains("list")) {
            return "List: Danh sách, có thể thay đổi kích thước. Ví dụ: List<Integer> list = new ArrayList<>();";
        }

        if (userMsg.contains("stack")) {
            return "Stack: Ngăn xếp, LIFO (vào sau ra trước).";
        }

        if (userMsg.contains("queue")) {
            return "Queue: Hàng đợi, FIFO (vào trước ra trước).";
        }

        if (userMsg.contains("loop")) {
            return "Loop: Vòng lặp (for, while, do-while).";
        }

        if (userMsg.contains("condition")) {
            return "Condition: Câu lệnh điều kiện (if / else / switch).";
        }

        if (userMsg.contains("recursion")) {
            return "Recursion: Đệ quy, hàm gọi lại chính nó.";
        }

        if (userMsg.contains("api")) {
            return "API: Giao diện lập trình ứng dụng, dùng để tương tác giữa phần mềm.";
        }

        if (userMsg.contains("framework")) {
            return "Framework: Khung phần mềm, giúp phát triển ứng dụng nhanh hơn.";
        }

        if (userMsg.contains("library")) {
            return "Library: Thư viện, tập hợp hàm/mã dùng sẵn.";
        }

        if (userMsg.contains("debug")) {
            return "Debug: Gỡ lỗi, tìm và sửa lỗi chương trình.";
        }

        if (userMsg.contains("ide")) {
            return "IDE: Môi trường phát triển tích hợp (IntelliJ, VSCode, Eclipse…).";
        }

        if (userMsg.contains("compilation")) {
            return "Compilation: Biên dịch, chuyển code thành ngôn ngữ máy.";
        }

        if (userMsg.contains("runtime")) {
            return "Runtime: Thời gian chạy, khi chương trình đang thực thi.";
        }

        if (userMsg.contains("exception")) {
            return "Exception: Ngoại lệ, lỗi xảy ra trong quá trình chạy.";
        }

        if (userMsg.contains("git")) {
            return "Git: Công cụ quản lý mã nguồn.";
        }

        if (userMsg.contains("sql")) {
            return "SQL: Ngôn ngữ truy vấn cơ sở dữ liệu.";
        }

        if (userMsg.contains("nosql")) {
            return "NoSQL: Cơ sở dữ liệu không quan hệ.";
        }

        if (userMsg.contains("frontend")) {
            return "Frontend: Phần giao diện người dùng.";
        }

        if (userMsg.contains("backend")) {
            return "Backend: Phần xử lý logic phía server.";
        }

        if (userMsg.contains("fullstack")) {
            return "Fullstack: Phát triển cả Frontend & Backend.";
        }

        if (userMsg.contains("http")) {
            return "HTTP: Giao thức truyền tải dữ liệu web.";
        }

        if (userMsg.contains("https")) {
            return "HTTPS: HTTP có bảo mật.";
        }

        if (userMsg.contains("rest api")) {
            return "REST API: API theo kiến trúc REST.";
        }

        if (userMsg.contains("json")) {
            return "JSON: Định dạng dữ liệu trao đổi (key-value).";
        }

        if (userMsg.contains("mvc")) {
            return "MVC: Model-View-Controller, mô hình thiết kế phần mềm.";
        }

        if (userMsg.contains("cli")) {
            return "CLI: Giao diện dòng lệnh.";
        }

        if (userMsg.contains("regex")) {
            return "Regex: Biểu thức chính quy, dùng để tìm kiếm / thay thế dữ liệu.";
        }
        if (userMsg.contains("function") || userMsg.contains("method")) return "Function / Method: Hàm, dùng để thực hiện 1 tác vụ. Ví dụ: void sayHello() { ... }";
        if (userMsg.contains("phim") || userMsg.contains("movie") || userMsg.contains("nhac") || userMsg.contains("music")) {
            return "🎬 Giải trí: Xem phim Marvel, anime, phim Việt hay nhạc Kpop, nhạc US-UK. Giải trí vừa đủ để refresh đầu óc.";
        }
        if (userMsg.contains("du lich") || userMsg.contains("travel") || userMsg.contains("dia diem")) {
            return "✈️ Du lịch gợi ý: Việt Nam - Đà Nẵng, Hội An, Sapa. Thế giới - Nhật Bản, Thái Lan, Úc. Chuẩn bị kỹ, check weather & itinerary trước.";
        }
        if (userMsg.contains("hoc tieng anh") || userMsg.contains("english learning") || userMsg.contains("learn english")) {
            return "📚 Mẹo học tiếng Anh: Nghe podcast, xem phim không sub, nói hàng ngày, học từ vựng theo chủ đề.";
        }

        if (userMsg.contains("hoc bai") || userMsg.contains("study tips")) {
            return "📝 Mẹo học tập: Chia nhỏ bài học, ôn lại thường xuyên, đặt mục tiêu hằng ngày, nghỉ ngơi đúng giờ.";
        }

        return "🤖 Fitty chưa hiểu rõ câu hỏi. Bạn có thể hỏi về bài tập, dinh dưỡng hoặc nghỉ ngơi nhé! | I’m not sure I understand yet. Ask me about workouts, nutrition, or health!";
    }
}
