<div align="center">
<img width="800" height="446" alt="5010385006886587409" src="https://github.com/user-attachments/assets/8368aafb-f680-42c0-87a4-6988c4ba0310" />
</div>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>اپلیکیشن سیفرگارد</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: linear-gradient(145deg, #0b1a2e, #1a2f44);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', 'Tahoma', 'Vazirmatn', system-ui, sans-serif;
            padding: 20px;
        }

        .card {
            max-width: 820px;
            width: 100%;
            background: rgba(255, 255, 255, 0.07);
            backdrop-filter: blur(14px);
            -webkit-backdrop-filter: blur(14px);
            border-radius: 48px;
            padding: 40px 36px;
            box-shadow: 0 25px 50px -8px rgba(0, 0, 0, 0.8), 0 0 0 1px rgba(255, 255, 255, 0.04);
            border: 1px solid rgba(255, 255, 255, 0.06);
            transition: all 0.25s ease;
        }

        .card:hover {
            box-shadow: 0 35px 60px -12px rgba(0, 0, 0, 0.9), 0 0 0 1px rgba(255, 215, 100, 0.15);
        }

        .badge {
            display: inline-block;
            background: rgba(255, 215, 100, 0.15);
            border: 1px solid rgba(255, 215, 100, 0.25);
            color: #f7d98c;
            font-size: 0.75rem;
            font-weight: 600;
            letter-spacing: 0.5px;
            padding: 6px 18px;
            border-radius: 100px;
            margin-bottom: 24px;
            backdrop-filter: blur(4px);
        }

        h1 {
            font-size: 2.4rem;
            font-weight: 700;
            color: #f0f4fa;
            margin-bottom: 16px;
            letter-spacing: -0.5px;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 8px 14px;
        }

        h1 .highlight {
            background: linear-gradient(135deg, #f7d875, #f5b84a);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
            font-weight: 800;
            text-shadow: 0 0 20px rgba(245, 184, 74, 0.2);
        }

        .sub {
            font-size: 1rem;
            color: #a0bbd4;
            margin-bottom: 28px;
            border-right: 3px solid #f5b84a;
            padding-right: 18px;
            background: rgba(255, 255, 255, 0.02);
            border-radius: 0 12px 12px 0;
            line-height: 1.6;
        }

        .content {
            color: #d6e2ef;
            line-height: 1.9;
            font-size: 1.05rem;
        }

        .content p {
            margin-bottom: 22px;
            text-align: justify;
        }

        .content strong {
            color: #f5d58a;
            font-weight: 600;
        }

        .content .emph {
            color: #b6d0e8;
            background: rgba(255, 215, 100, 0.06);
            padding: 0 6px;
            border-radius: 8px;
            font-weight: 500;
        }

        .divider {
            width: 60px;
            height: 2px;
            background: linear-gradient(90deg, #f5b84a, transparent);
            margin: 28px 0 24px 0;
            border-radius: 4px;
        }

        .footer-note {
            background: rgba(0, 0, 0, 0.25);
            border-radius: 24px;
            padding: 20px 24px;
            border: 1px solid rgba(255, 215, 100, 0.08);
            margin-top: 10px;
            color: #b8cee5;
            font-size: 0.95rem;
            backdrop-filter: blur(4px);
        }

        .footer-note strong {
            color: #f5d58a;
        }

        .footer-note i {
            color: #9bb7d4;
            font-style: normal;
            display: inline-block;
            margin-left: 6px;
        }

        .chip {
            display: inline-block;
            background: rgba(255, 255, 255, 0.04);
            padding: 2px 14px;
            border-radius: 100px;
            border: 1px solid rgba(255, 255, 255, 0.04);
            font-size: 0.8rem;
            color: #b8cee5;
        }

        /* راست‌چینی کامل */
        .rtl {
            direction: rtl;
            text-align: right;
        }

        /* واکنش‌گرایی */
        @media (max-width: 540px) {
            .card {
                padding: 28px 18px;
                border-radius: 32px;
            }
            h1 {
                font-size: 1.8rem;
            }
            .content {
                font-size: 0.98rem;
            }
            .footer-note {
                padding: 16px 18px;
            }
        }
    </style>
</head>
<body>
    <div class="card rtl">
        <!-- برچسب کوچک -->
        <div class="badge">🔐 رمزنگار هوشمند</div>

        <!-- عنوان اصلی -->
        <h1>
            <span>اپلیکیشن</span>
            <span class="highlight">سیفرگارد</span>
        </h1>

        <!-- زیرنویس -->
        <div class="sub">
            ✦ ساخته‌شده با هوش مصنوعی گوگل · رابط کاربری فارسی
        </div>

        <!-- متن اصلی -->
        <div class="content">
            <p>
                <strong>اپلیگیشن رمزنگار سیفرگارد</strong> یک اپلیکیشن ساده و فارسی است که با 
                <span class="emph">هوش مصنوعی گوگل</span> ساخته شده است تا به شما کمک کند 
                در موارد بحرانی و خاص پیام‌های خود را به صورت <strong>رمز شده</strong> ارسال کنید 
                و فقط به فرد مربوطه از قبل یا به نحوی کلید مخصوص را اطلاع دهید. 
                به این ترتیب شما می‌توانید از پیام‌ها و متن‌های خود محافظت کنید.
            </p>

            <p>
                موارد استفاده این اپ بسته به فعالیت شما بسیار زیاد است اما ارسال 
                <strong>گانفیگ‌های مورد نیاز</strong> از طریق اپ‌های داخلی یا پیامگ 
                به صورت رمز شده یکی از موارد مد نظر من بوده است.
            </p>

            <!-- خط جداکننده -->
            <div class="divider"></div>

            <p style="color:#c8dbea;">
                من <strong>هیچ مسئولیتی</strong> در قبال استفاده نادرست و یا حساس از این اپ ندارم 
                و صرفاً بابت <span class="emph">علاقه‌مندی</span> و آزمون هوش مصنوعی 
                این اپ را ساخته‌ام و در اختیار عموم قرار می‌دهم.
            </p>
        </div>

        <!-- بخش پایانی با نکته‌های زیبا -->
        <div class="footer-note">
            <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap; margin-bottom: 8px;">
                <span style="font-size: 1.3rem;">⚡</span>
                <span><strong>توجه:</strong> این نسخه برای آزمون و یادگیری طراحی شده.</span>
                <span class="chip">#CipherGuard</span>
            </div>
            <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap; opacity: 0.7; font-size: 0.9rem;">
                <span>🤖 ساخته‌شده با ❤️ و Gemini</span>
                <span style="margin: 0 6px;">·</span>
                <span>نسخه ۱.۰ · فارسی</span>
            </div>
        </div>
    </div>
</body>
</html>.
<div align="center">
<img width="446" height="800" alt="5010385006886587410" src="https://github.com/user-attachments/assets/19c8c000-dd18-4740-8034-1c98274683ec" />
</div>

<div align="center">
<img width="800" height="446" alt="5010385006886587408" src="https://github.com/user-attachments/assets/d3516aa9-54ef-46a2-b023-5d9a2a2967ff" />
</div>

