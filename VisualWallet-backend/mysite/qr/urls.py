from django.urls import path
from django.conf.urls import url
from . import views


app_name = "qr"
urlpatterns = [
    # path('', views.IndexView.as_view(), name='index'),
    path('generate/', views.genSplit, name='genSplit'),
    path('validate/', views.validate, name='validate'),
]