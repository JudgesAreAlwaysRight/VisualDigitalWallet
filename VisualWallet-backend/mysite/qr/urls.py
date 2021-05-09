from django.urls import path
from django.conf.urls import url
from . import views


app_name = "qr"
urlpatterns = [
    path('generate/', views.genSplit, name='genSplit'),
    path('validate/', views.validate, name='validate'),
]