package com.mahmutgunduz.edukids.data.model

data class ParentModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val backgroundColor: String,
    val notificationCount: Int = 0,
    val lastUpdate: String? = null,
    val isNew: Boolean = false
)

object ParentModules {
    fun getAllModules(): List<ParentModule> {
        return listOf(
            ParentModule(
                id = "daily_report",
                title = "Günlük Rapor",
                subtitle = "Çocuğumun bugünü",
                icon = "📊",
                backgroundColor = "#E8F5E8",
                notificationCount = 2,
                lastUpdate = "5 dk önce"
            ),
            ParentModule(
                id = "live_tracking",
                title = "Canlı Takip",
                subtitle = "Şu anda ne yapıyor?",
                icon = "📍",
                backgroundColor = "#FFF3E0",
                notificationCount = 0,
                isNew = true
            ),
            ParentModule(
                id = "photo_gallery",
                title = "Fotoğraf Galerisi",
                subtitle = "Bugünkü anılar",
                icon = "📸",
                backgroundColor = "#E3F2FD",
                notificationCount = 7,
                lastUpdate = "2 dk önce"
            ),
            ParentModule(
                id = "development_tracking",
                title = "Gelişim Takibi",
                subtitle = "Adım adım büyüyor",
                icon = "📈",
                backgroundColor = "#F3E5F5",
                notificationCount = 1,
                lastUpdate = "1 saat önce"
            ),
            ParentModule(
                id = "attendance",
                title = "Devam Durumu",
                subtitle = "Okula geliş-gidiş",
                icon = "✅",
                backgroundColor = "#E8F5E8",
                notificationCount = 0
            ),
            ParentModule(
                id = "meals",
                title = "Beslenme Takibi",
                subtitle = "Ne yedi, ne içti?",
                icon = "🍽️",
                backgroundColor = "#FFF8E1",
                notificationCount = 3,
                lastUpdate = "30 dk önce"
            ),
            ParentModule(
                id = "sleep_tracking",
                title = "Uyku Takibi",
                subtitle = "Uyku saatleri",
                icon = "😴",
                backgroundColor = "#E1F5FE",
                notificationCount = 0
            ),
            ParentModule(
                id = "activities",
                title = "Etkinlikler",
                subtitle = "Bugünkü aktiviteler",
                icon = "🎨",
                backgroundColor = "#FCE4EC",
                notificationCount = 4,
                lastUpdate = "15 dk önce"
            ),
            ParentModule(
                id = "teacher_messages",
                title = "Öğretmen Mesajları",
                subtitle = "Özel notlar ve mesajlar",
                icon = "💬",
                backgroundColor = "#F1F8E9",
                notificationCount = 2,
                lastUpdate = "10 dk önce"
            ),
            ParentModule(
                id = "health_tracking",
                title = "Sağlık Takibi",
                subtitle = "İlaç, ateş, genel durum",
                icon = "🏥",
                backgroundColor = "#FFEBEE",
                notificationCount = 0
            ),
            ParentModule(
                id = "behavior_notes",
                title = "Davranış Notları",
                subtitle = "Sosyal gelişim",
                icon = "⭐",
                backgroundColor = "#FFF9C4",
                notificationCount = 1,
                lastUpdate = "45 dk önce"
            ),
            ParentModule(
                id = "homework",
                title = "Ev Ödevleri",
                subtitle = "Yapılacaklar listesi",
                icon = "📝",
                backgroundColor = "#E8EAF6",
                notificationCount = 2,
                lastUpdate = "2 saat önce"
            ),
            ParentModule(
                id = "announcements",
                title = "Duyurular",
                subtitle = "Okul haberleri",
                icon = "📢",
                backgroundColor = "#E0F2F1",
                notificationCount = 3,
                lastUpdate = "1 saat önce"
            ),
            ParentModule(
                id = "events",
                title = "Etkinlik Takvimi",
                subtitle = "Yaklaşan etkinlikler",
                icon = "📅",
                backgroundColor = "#FFF3E0",
                notificationCount = 1,
                lastUpdate = "3 saat önce"
            ),
            ParentModule(
                id = "progress_reports",
                title = "İlerleme Raporları",
                subtitle = "Aylık değerlendirme",
                icon = "📋",
                backgroundColor = "#F3E5F5",
                notificationCount = 0
            ),
            ParentModule(
                id = "video_calls",
                title = "Video Görüşme",
                subtitle = "Öğretmenle görüş",
                icon = "📹",
                backgroundColor = "#E8F5E8",
                notificationCount = 0,
                isNew = true
            ),
            ParentModule(
                id = "emergency_contacts",
                title = "Acil Durum",
                subtitle = "Hızlı iletişim",
                icon = "🚨",
                backgroundColor = "#FFEBEE",
                notificationCount = 0
            ),
            ParentModule(
                id = "payment_tracking",
                title = "Ödeme Takibi",
                subtitle = "Ödemeler ve faturalar",
                icon = "💳",
                backgroundColor = "#E1F5FE",
                notificationCount = 1,
                lastUpdate = "1 gün önce"
            ),
            ParentModule(
                id = "transportation",
                title = "Ulaşım Takibi",
                subtitle = "Servis bilgileri",
                icon = "🚌",
                backgroundColor = "#FFF8E1",
                notificationCount = 0
            ),
            ParentModule(
                id = "feedback",
                title = "Geri Bildirim",
                subtitle = "Görüş ve öneriler",
                icon = "💭",
                backgroundColor = "#F1F8E9",
                notificationCount = 0
            )
        )
    }
}