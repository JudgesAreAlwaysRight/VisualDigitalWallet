from django.urls import path
from django.conf.urls import url
from . import views


app_name = "qr"
urlpatterns = [
    # path('', views.IndexView.as_view(), name='index'),
    path('test/', views.testGet, name='testget'),
]