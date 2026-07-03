import React, { useState } from 'react';
import { Page } from '@/shared/types/appTypes';
import { motion } from 'motion/react';
import { ArrowLeft, ShieldCheck } from 'lucide-react';
import { toast } from 'sonner';

interface SignupProps {
  onNavigate: (page: Page) => void;
}

export function Signup({ onNavigate }: SignupProps) {
  const [formData, setFormData] = useState({
    id: '',
    password: '',
    confirmPassword: '',
    name: '',
    department: ''
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.id) newErrors.id = '아이디를 입력해주세요';
    if (!formData.password) newErrors.password = '비밀번호를 입력해주세요';
    if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = '비밀번호가 일치하지 않습니다';
    if (!formData.name) newErrors.name = '이름을 입력해주세요';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSignup = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      // Save to localStorage
      const users = JSON.parse(localStorage.getItem('if_users') || '[]');

      // Check duplicate ID
      if (users.some((u: any) => u.id === formData.id)) {
        setErrors({ ...errors, id: '이미 존재하는 아이디입니다' });
        return;
      }

      users.push({
        id: formData.id,
        password: formData.password,
        name: formData.name,
        department: formData.department
      });

      localStorage.setItem('if_users', JSON.stringify(users));
      toast.success('회원가입이 완료되었습니다.');
      onNavigate('login');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.5 }}
        className="bg-white w-full max-w-5xl rounded-2xl shadow-2xl overflow-hidden flex h-auto md:h-[750px]"
      >
        {/* Left Side - Branding (No Image) */}
        <div className="hidden md:flex w-1/2 relative bg-gradient-to-br from-[#2F8F6B] to-[#1e6b4e] items-center justify-center overflow-hidden">
          {/* Decorative Circles */}
          <div className="absolute top-[-20%] left-[-20%] w-[80%] h-[80%] rounded-full border-[2px] border-white/10" />
          <div className="absolute bottom-[-10%] right-[-10%] w-[60%] h-[60%] rounded-full bg-white/5" />

          <div className="relative z-20 text-white p-12">
            <div className="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-2xl mb-8 flex items-center justify-center border border-white/30 shadow-lg">
              <span className="text-white text-4xl font-bold">If</span>
            </div>
            <h2 className="text-4xl font-bold mb-6">관리자 계정 생성</h2>
            <p className="text-white/90 text-lg leading-relaxed">
              If 시스템의 관리자가 되어<br/>
              고령자 안전 관리에 동참해주세요.
            </p>

            <div className="mt-12 flex gap-4">
              <div className="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-lg backdrop-blur-sm">
                <ShieldCheck size={20} />
                <span className="text-sm font-medium">안전성 평가</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Side - Signup Form */}
        <div className="w-full md:w-1/2 p-8 md:p-12 flex flex-col justify-center relative">
          <button
            onClick={() => onNavigate('login')}
            className="absolute top-8 left-8 flex items-center text-gray-400 hover:text-gray-800 transition-colors group"
          >
            <ArrowLeft size={20} className="mr-1 group-hover:-translate-x-1 transition-transform" />
            로그인으로 돌아가기
          </button>

          <div className="max-w-md mx-auto w-full mt-8">
            <div className="mb-8">
              <h1 className="text-3xl font-bold text-gray-800 mb-2">회원가입</h1>
              <p className="text-gray-500">새로운 관리자 정보를 입력해주세요.</p>
            </div>

            <form onSubmit={handleSignup} className="space-y-4">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">아이디</label>
                <input
                  type="text"
                  value={formData.id}
                  onChange={(e) => setFormData({ ...formData, id: e.target.value })}
                  className={`w-full px-5 py-3.5 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] transition-all ${errors.id ? 'border-red-500' : 'border-gray-200'}`}
                  placeholder="아이디를 입력하세요"
                />
                {errors.id && <p className="text-red-500 text-xs mt-1">{errors.id}</p>}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-gray-700 mb-2">이름</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className={`w-full px-5 py-3.5 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] transition-all ${errors.name ? 'border-red-500' : 'border-gray-200'}`}
                    placeholder="홍길동"
                  />
                  {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
                </div>
                <div>
                  <label className="block text-sm font-bold text-gray-700 mb-2">부서 (선택)</label>
                  <input
                    type="text"
                    value={formData.department}
                    onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                    className="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] transition-all"
                    placeholder="복지팀"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">비밀번호</label>
                <input
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className={`w-full px-5 py-3.5 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] transition-all ${errors.password ? 'border-red-500' : 'border-gray-200'}`}
                  placeholder="비밀번호"
                />
                {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">비밀번호 확인</label>
                <input
                  type="password"
                  value={formData.confirmPassword}
                  onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                  className={`w-full px-5 py-3.5 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2F8F6B] transition-all ${errors.confirmPassword ? 'border-red-500' : 'border-gray-200'}`}
                  placeholder="비밀번호 확인"
                />
                {errors.confirmPassword && <p className="text-red-500 text-xs mt-1">{errors.confirmPassword}</p>}
              </div>

              <button
                type="submit"
                className="w-full bg-[#2F8F6B] hover:bg-[#257A5A] text-white font-bold py-4 rounded-xl shadow-lg shadow-[#2F8F6B]/30 transition-all transform active:scale-[0.99] mt-6"
              >
                가입하기
              </button>
            </form>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
